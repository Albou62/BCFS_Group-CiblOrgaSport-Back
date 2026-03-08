package com.ciblorgasport.user_service.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SportifProfile {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 @Column(unique = true, nullable = true)
 private Long authUserId; 
 private String firstName;
 private String lastName;
 private String licenseNumber;
 private String club;
 private boolean trackingAccepted = true;
 private String username;
 @Transient 
 private List<Document> documents;
 public List<Document> getDocuments() {
	return documents;
}
 public void setDocuments(List<Document> documents) {
	this.documents = documents;
 }
 public String getUsername() {
	return username;
}
 public void setUsername(String username) {
	this.username = username;
 }
 public Long getId() {
	return id;
 }
 public void setId(Long id) {
	this.id = id;
 }
 public Long getAuthUserId() {
	return authUserId;
 }
 public void setAuthUserId(Long authUserId) {
	this.authUserId = authUserId;
 }
 public String getFirstName() {
	return firstName;
 }
 public void setFirstName(String firstName) {
	this.firstName = firstName;
 }
 public String getLastName() {
	return lastName;
 }
 public void setLastName(String lastName) {
	this.lastName = lastName;
 }
 public String getLicenseNumber() {
	return licenseNumber;
 }
 public void setLicenseNumber(String licenseNumber) {
	this.licenseNumber = licenseNumber;
 }
 public String getClub() {
	return club;
 }
 public void setClub(String club) {
	this.club = club;
 }
 public boolean isTrackingAccepted() {
	return trackingAccepted;
 }
 public void setTrackingAccepted(boolean trackingAccepted) {
	this.trackingAccepted = trackingAccepted;
 } 
}
