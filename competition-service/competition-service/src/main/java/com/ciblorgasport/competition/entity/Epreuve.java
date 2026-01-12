package com.ciblorgasport.competition.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "epreuve")
public class Epreuve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;                 // name_epreuve

    private LocalDateTime horaireAthletes;
    private LocalDateTime horairePublic;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    // Getters / setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getHoraireAthletes() {
        return horaireAthletes;
    }

    public void setHoraireAthletes(LocalDateTime horaireAthletes) {
        this.horaireAthletes = horaireAthletes;
    }

    public LocalDateTime getHorairePublic() {
        return horairePublic;
    }

    public void setHorairePublic(LocalDateTime horairePublic) {
        this.horairePublic = horairePublic;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
