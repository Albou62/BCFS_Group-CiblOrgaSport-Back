package com.ciblorgasport.competition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "manche")
public class Manche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "epreuve_id")
    private Epreuve epreuve;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_classement", nullable = false)
    private TypeClassement typeClassement;

    @Column
    private Integer ordre;

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

    public Epreuve getEpreuve() {
        return epreuve;
    }

    public void setEpreuve(Epreuve epreuve) {
        this.epreuve = epreuve;
    }

    public TypeClassement getTypeClassement() {
        return typeClassement;
    }

    public void setTypeClassement(TypeClassement typeClassement) {
        this.typeClassement = typeClassement;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
