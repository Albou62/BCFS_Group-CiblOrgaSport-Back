package com.ciblorgasport.competition.dto;

public record RegistrationResponse(
    UUID registrationId,
    UUID competitionId,
    String competitionName,
    UUID athleteId,
    String athleteDisplayName,
    RegistrationStatus status,
    LocalDateTime registeredAt,
    Integer bibNumber
) {}