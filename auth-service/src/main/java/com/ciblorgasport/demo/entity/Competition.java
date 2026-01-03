package com.ciblorgasport.demo.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="COMPETITION")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Date dateDebut;

    @Column(nullable = true)
    public Date dateFin;

    @OneToMany(mappedBy = "competition")
    public List<Sportif> participants;

    @Column(nullable = false)
    public String modalite;

    public void finir(Date dateFin) {
        this.dateFin = dateFin;
    }
}
