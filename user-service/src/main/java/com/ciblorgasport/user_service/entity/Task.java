package com.ciblorgasport.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Task {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private String title;
 private String status; // "À venir", "En cours", "Terminé"
 private String timeSlot; // ex: "08:00 - 12:00" pour coller à votre UI
 private LocalDateTime startTime;

 @ManyToOne
 @JoinColumn(name = "volontaire_id")
 private VolontaireProfile volontaire;

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public String getTitle() {
	return title;
 }

 public void setTitle(String title) {
	this.title = title;
 }

 public String getStatus() {
	return status;
 }

 public void setStatus(String status) {
	this.status = status;
 }

 public String getTimeSlot() {
	return timeSlot;
 }

 public void setTimeSlot(String timeSlot) {
	this.timeSlot = timeSlot;
 }

 public LocalDateTime getStartTime() {
	return startTime;
 }

 public void setStartTime(LocalDateTime startTime) {
	this.startTime = startTime;
 }

 public VolontaireProfile getVolontaire() {
	return volontaire;
 }

 public void setVolontaire(VolontaireProfile volontaire) {
	this.volontaire = volontaire;
 }
}