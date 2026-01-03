package com.ciblorgasport.demo.entity;

import java.util.List;

import jakarta.persistence.Entity;

@Entity
public class Volontaire extends User {

    public List<Programme> programme;
}
