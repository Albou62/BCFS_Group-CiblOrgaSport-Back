package com.ciblorgasport.demo.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Manche extends Competition {
    @ManyToOne
    @JoinColumn(name="epreuve_id")
    public Epreuve epreuve;
}
