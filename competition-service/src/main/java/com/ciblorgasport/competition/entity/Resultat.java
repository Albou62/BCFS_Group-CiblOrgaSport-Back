package com.ciblorgasport.competition.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "resultat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"athlete_id", "manche_id"}))
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manche_id")
    private Manche manche;

    @Column(precision = 10, scale = 3)
    private BigDecimal score;

    @Column(columnDefinition = "TIME")
    private LocalTime temps;

    @Column
    private Integer rang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutResultat statut = StatutResultat.VALIDE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMedaille medaille = TypeMedaille.AUCUNE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public Manche getManche() {
        return manche;
    }

    public void setManche(Manche manche) {
        this.manche = manche;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalTime getTemps() {
        return temps;
    }

    public void setTemps(LocalTime temps) {
        this.temps = temps;
    }

    public Integer getRang() {
        return rang;
    }

    public void setRang(Integer rang) {
        this.rang = rang;
    }

    public StatutResultat getStatut() {
        return statut;
    }

    public void setStatut(StatutResultat statut) {
        this.statut = statut;
    }

    public TypeMedaille getMedaille() {
        return medaille;
    }

    public void setMedaille(TypeMedaille medaille) {
        this.medaille = medaille;
    }
}
