package com.ciblorgasport.competition.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "athletes")
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;       // foreign key to user-service, not a JPA join
    private String displayName;
    private String sport;
    private String nationality;
}