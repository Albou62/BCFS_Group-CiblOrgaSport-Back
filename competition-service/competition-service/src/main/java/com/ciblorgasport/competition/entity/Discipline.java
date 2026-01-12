package com.ciblorgasport.competition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "discipline")
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;           // name_discipline

    @Column(nullable = false)
    private boolean collectif;     // collectif

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

    public boolean isCollectif() {
        return collectif;
    }

    public void setCollectif(boolean collectif) {
        this.collectif = collectif;
    }
}
