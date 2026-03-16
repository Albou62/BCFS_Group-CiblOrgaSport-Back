package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.CompetitionDto;
import com.ciblorgasport.competition.dto.CreateCompetitionRequest;
import com.ciblorgasport.competition.dto.RegistrationRequest;
import com.ciblorgasport.competition.dto.RegistrationResponse;
import com.ciblorgasport.competition.service.CompetitionService;
import com.ciblorgasport.competition.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService service;
    private final RegistrationService registrationService;

    public CompetitionController(CompetitionService service,
                                 RegistrationService registrationService) {
        this.service = service;
        this.registrationService = registrationService;
    }

    // -------------------------------------------------------------------------
    // Existing competition routes — unchanged
    // -------------------------------------------------------------------------

    @GetMapping
    public List<CompetitionDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CompetitionDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public CompetitionDto create(@RequestBody CreateCompetitionRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CompetitionDto update(@PathVariable Long id,
                                 @RequestBody CreateCompetitionRequest req) {
        return service.update(id, req);
    }

    @PostMapping("/{id}/finish")
    public CompetitionDto finish(@PathVariable Long id) {
        return service.finish(id);
    }

    /**
     * Register an athlete for a competition.
     * POST /api/competitions/{id}/registrations
     * Role: ATHLETE
     */
    @PostMapping("/{id}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(@PathVariable Long id,
                                         @RequestBody RegistrationRequest req) {
        return registrationService.registerAthlete(id, req);
    }

    /**
     * List all registrations for a competition.
     * GET /api/competitions/{id}/registrations
     * Role: ORGANIZER
     */
    @GetMapping("/{id}/registrations")
    public List<RegistrationResponse> listRegistrations(@PathVariable Long id) {
        return registrationService.getRegistrationsForCompetition(id);
    }

    /**
     * Confirm a pending registration.
     * PUT /api/competitions/{id}/registrations/{registrationId}/confirm
     * Role: ORGANIZER
     */
    @PutMapping("/{id}/registrations/{registrationId}/confirm")
    public RegistrationResponse confirm(@PathVariable Long id,
                                        @PathVariable Long registrationId) {
        return registrationService.confirmRegistration(id, registrationId);
    }

    /**
     * Withdraw a registration.
     * DELETE /api/competitions/{id}/registrations/{registrationId}
     * Role: ATHLETE (own registration) or ORGANIZER
     *
     * The requesting user's ID comes from the JWT forwarded by the gateway.
     * It is expected as a request header X-User-Id set by the auth filter.
     */
    @DeleteMapping("/{id}/registrations/{registrationId}")
    public RegistrationResponse withdraw(@PathVariable Long id,
                                         @PathVariable Long registrationId,
                                         @RequestHeader("X-User-Id") UUID requestingUserId) {
        return registrationService.withdrawRegistration(id, registrationId, requestingUserId);
    }

    /**
     * List all registrations for a given athlete across all competitions.
     * GET /api/competitions/athletes/{athleteId}/registrations
     * Role: ATHLETE (self)
     */
    @GetMapping("/athletes/{athleteId}/registrations")
    public List<RegistrationResponse> listForAthlete(@PathVariable Long athleteId) {
        return registrationService.getRegistrationsForAthlete(athleteId);
    }
}