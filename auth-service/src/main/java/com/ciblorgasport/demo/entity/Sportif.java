package com.ciblorgasport.demo.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Sportif extends User {

    @Column(nullable = false)
    public boolean conformiteCharte;

    @Column(nullable = false)
    public Date dateInscription;

    @Column(nullable = false)
    public boolean localisationActive;

    @Column(nullable = false)
    public String nationalite;
}
