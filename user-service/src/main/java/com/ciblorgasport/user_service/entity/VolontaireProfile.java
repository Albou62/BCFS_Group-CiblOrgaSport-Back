package com.ciblorgasport.user_service.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VolontaireProfile {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 @Column(unique = true, nullable = false)
 private Long authUserId;
 private String firstName;
 private String lastName;
 private String skills;
 private String username;
 
 @OneToMany(mappedBy = "volontaire", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
 @JsonManagedReference 
 private List<Task> tasks = new ArrayList<>();
 public List<Task> getTasks() {
     return tasks;
 }

 public void setTasks(List<Task> tasks) {
     this.tasks = tasks;
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
 public String getSkills() {
	return skills;
 }
 public void setSkills(String skills) {
	this.skills = skills;
 } 
}