package com.ciblorgasport.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Document {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private Long uploaderAuthId;
 private String athleteName; // Pour l'affichage direct dans le tableau d'Arthur
 private String type;      // "Passeport", "Certificat"
 private String fileName;
 private String status;    // "EN_ATTENTE", "VALIDE", "REFUSE"
 private String uploaderUsername;
 
 public String getUploaderUsername() { return uploaderUsername; }
 public void setUploaderUsername(String uploaderUsername) { this.uploaderUsername = uploaderUsername; }
 public Long getId() {
	return id;
 }
 public void setId(Long id) {
	this.id = id;
 }
 public Long getUploaderAuthId() {
	return uploaderAuthId;
 }
 public void setUploaderAuthId(Long uploaderAuthId) {
	this.uploaderAuthId = uploaderAuthId;
 }
 public String getAthleteName() {
	return athleteName;
 }
 public void setAthleteName(String athleteName) {
	this.athleteName = athleteName;
 }
 public String getType() {
	return type;
 }
 public void setType(String type) {
	this.type = type;
 }
 public String getFileName() {
	return fileName;
 }
 public void setFileName(String fileName) {
	this.fileName = fileName;
 }
 public String getStatus() {
	return status;
 }
 public void setStatus(String status) {
	this.status = status;
 }
}