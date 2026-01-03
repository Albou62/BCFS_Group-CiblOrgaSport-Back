package com.ciblorgasport.demo.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Notification {

    @Column(nullable = false)
    public Date date;

    @Column(nullable = false)
    public String lieu;

    @Column(nullable = false)
    public String titre;

    @Column(nullable = false)
    public String details;

    @Column(nullable = false)
    public String notificationType;
}
