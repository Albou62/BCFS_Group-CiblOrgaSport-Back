package com.ciblorgasport.competitionservice.service;

import com.ciblorgasport.competitionservice.dto.NotificationRequest;
import com.ciblorgasport.competitionservice.dto.RegistrationRequest;
import com.ciblorgasport.competitionservice.dto.RegistrationResponse;
import com.ciblorgasport.competitionservice.entity.Athlete;
import com.ciblorgasport.competitionservice.entity.Competition;
import com.ciblorgasport.competitionservice.entity.CompetitionRegistration;
import com.ciblorgasport.competitionservice.entity.RegistrationStatus;
import com.ciblorgasport.competitionservice.repository.AthleteRepository;
import com.ciblorgasport.competitionservice.repository.CompetitionRegistrationRepository;
import com.ciblorgasport.competitionservice.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final CompetitionRepository competitionRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRegistrationRepository registrationRepository;

    // WebClient for calling the notification-service.
    // Adapt the bean name if your project already declares a WebClient differently.
    private final WebClient.Builder webClientBuilder;

    // Set in application.yml: notification-service.url=http://notification-service:8080
    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    // -------------------------------------------------------------------------
    // Register an athlete for a competition
    // -------------------------------------------------------------------------

    @Transactional
    public RegistrationResponse registerAthlete(UUID competitionId, RegistrationRequest request) {

        // 1. Fetch competition — 404 if not found
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Competition not found: " + competitionId));

        // 2. Competition must be OPEN for new registrations
        if (!"OPEN".equalsIgnoreCase(competition.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Competition is not open for registration (current status: " + competition.getStatus() + ")");
        }

        // 3. Registration deadline check
        if (competition.getRegistrationDeadline() != null
                && LocalDateTime.now().isAfter(competition.getRegistrationDeadline())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Registration deadline has passed: " + competition.getRegistrationDeadline());
        }

        // 4. Capacity check — only count CONFIRMED spots, PENDING ones are soft-reserved
        if (competition.getMaxAthletes() != null) {
            long confirmed = registrationRepository.countByCompetitionIdAndStatus(
                    competitionId, RegistrationStatus.CONFIRMED);
            if (confirmed >= competition.getMaxAthletes()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Competition is full (" + competition.getMaxAthletes() + " athletes max)");
            }
        }

        // 5. Find or create the Athlete record for this userId
        Athlete athlete = athleteRepository.findByUserId(request.getUserId())
                .orElseGet(() -> athleteRepository.save(
                        Athlete.builder()
                                .userId(request.getUserId())
                                .displayName(request.getDisplayName())
                                .sport(request.getSport())
                                .nationality(request.getNationality())
                                .build()));

        // 6. Duplicate registration check
        registrationRepository.findByCompetitionIdAndAthleteId(competitionId, athlete.getId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Athlete is already registered for this competition (status: "
                                    + existing.getStatus() + ")");
                });

        // 7. Create the registration with PENDING status
        CompetitionRegistration registration = CompetitionRegistration.builder()
                .competition(competition)
                .athlete(athlete)
                .registeredAt(LocalDateTime.now())
                .status(RegistrationStatus.PENDING)
                .build();

        CompetitionRegistration saved = registrationRepository.save(registration);
        log.info("Athlete {} registered for competition {} — status PENDING", athlete.getId(), competitionId);

        // 8. Notify the athlete asynchronously (fire-and-forget, non-blocking)
        sendNotification(
                request.getUserId(),
                "Your registration for \"" + competition.getName() + "\" has been received and is pending confirmation.",
                "REGISTRATION_PENDING"
        );

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Confirm a pending registration (organizer action)
    // -------------------------------------------------------------------------

    @Transactional
    public RegistrationResponse confirmRegistration(UUID registrationId) {

        CompetitionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + registrationId));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only PENDING registrations can be confirmed (current status: "
                            + registration.getStatus() + ")");
        }

        // Assign a bib number = next integer after the highest existing one for this competition
        int nextBib = registrationRepository
                .findByCompetitionId(registration.getCompetition().getId())
                .stream()
                .filter(r -> r.getBibNumber() != null)
                .mapToInt(CompetitionRegistration::getBibNumber)
                .max()
                .orElse(0) + 1;

        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setBibNumber(nextBib);

        CompetitionRegistration saved = registrationRepository.save(registration);
        log.info("Registration {} confirmed — bib #{}", registrationId, nextBib);

        sendNotification(
                registration.getAthlete().getUserId(),
                "Your registration for \""
                        + registration.getCompetition().getName()
                        + "\" is confirmed! Your bib number is #" + nextBib + ".",
                "REGISTRATION_CONFIRMED"
        );

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Withdraw a registration (athlete self-service or organizer)
    // -------------------------------------------------------------------------

    @Transactional
    public RegistrationResponse withdrawRegistration(UUID registrationId, UUID requestingUserId) {

        CompetitionRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration not found: " + registrationId));

        // Authorization: the requesting user must own this registration.
        // Organizer-level override should be enforced at the controller layer via role check.
        boolean isOwner = registration.getAthlete().getUserId().equals(requestingUserId);
        if (!isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to withdraw this registration");
        }

        if (registration.getStatus() == RegistrationStatus.WITHDRAWN) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Registration is already withdrawn");
        }

        registration.setStatus(RegistrationStatus.WITHDRAWN);
        CompetitionRegistration saved = registrationRepository.save(registration);
        log.info("Registration {} withdrawn by user {}", registrationId, requestingUserId);

        sendNotification(
                registration.getAthlete().getUserId(),
                "You have successfully withdrawn from \""
                        + registration.getCompetition().getName() + "\".",
                "REGISTRATION_WITHDRAWN"
        );

        return toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read — list registrations for a competition (organizer view)
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsForCompetition(UUID competitionId) {

        if (!competitionRepository.existsById(competitionId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Competition not found: " + competitionId);
        }

        return registrationRepository.findByCompetitionId(competitionId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Read — list registrations for an athlete (athlete self-view)
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsForAthlete(UUID athleteId) {

        if (!athleteRepository.existsById(athleteId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Athlete not found: " + athleteId);
        }

        return registrationRepository.findByAthleteId(athleteId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Maps a CompetitionRegistration entity to its DTO.
     */
    private RegistrationResponse toResponse(CompetitionRegistration r) {
        return RegistrationResponse.builder()
                .registrationId(r.getId())
                .competitionId(r.getCompetition().getId())
                .competitionName(r.getCompetition().getName())
                .athleteId(r.getAthlete().getId())
                .athleteDisplayName(r.getAthlete().getDisplayName())
                .status(r.getStatus())
                .registeredAt(r.getRegisteredAt())
                .bibNumber(r.getBibNumber())
                .build();
    }

    /**
     * Fire-and-forget notification call to the notification-service.
     * Uses WebClient in a non-blocking way so a notification failure never
     * rolls back the main transaction.
     *
     * @param userId     the target user (from auth-service)
     * @param message    human-readable notification body
     * @param type       notification type key (e.g. "REGISTRATION_CONFIRMED")
     */
    private void sendNotification(UUID userId, String message, String type) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri(notificationServiceUrl + "/notifications")
                    .bodyValue(NotificationRequest.builder()
                            .userId(userId)
                            .message(message)
                            .type(type)
                            .build())
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                            response -> log.debug("Notification sent to user {}: [{}]", userId, type),
                            error   -> log.warn("Failed to send notification to user {}: {}", userId, error.getMessage())
                    );
        } catch (Exception e) {
            // Swallow — notification failures must never break the registration flow
            log.warn("Unexpected error while sending notification to user {}: {}", userId, e.getMessage());
        }
    }
}