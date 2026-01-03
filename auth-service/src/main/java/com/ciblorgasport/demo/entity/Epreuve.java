package com.ciblorgasport.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Epreuve extends Competition {
    @ManyToOne
    @JoinColumn(name="tournoi_id")
    public Tournoi tournoi;
}
