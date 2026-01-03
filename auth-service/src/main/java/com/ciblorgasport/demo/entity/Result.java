package com.ciblorgasport.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    public Competition competition;

    @ManyToOne
    @JoinColumn(name = "sportif_id")
    public Sportif sportif;

    @Column(nullable = false)
    public int result;

    public Result(Competition competition, Sportif sportif, int result) {
        this.competition = competition;
        this.sportif = sportif;
        this.result = result;
    }

    public Long getId() { return this.id; }
    public Competition getCompetition() { return this.competition; }
    public Sportif getSportif() { return this.sportif; }
    public int getResult() { return this.result; }

    public void setCompetition(Competition competition) { this.competition = competition; }
    public void setSportif(Sportif sportif) { this.sportif = sportif; }
    public void setResult(int result) { this.result = result; }
}
