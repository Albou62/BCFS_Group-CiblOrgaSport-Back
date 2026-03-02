package com.ciblorgasport.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ticket {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 private Long ownerAuthId;
 private String code;
 private String fileName;
 private String ownerUsername;
 
 public String getOwnerUsername() { return ownerUsername; }
 public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
 public Long getId() {
	return id;
 }
 public void setId(Long id) {
	this.id = id;
 }
 public Long getOwnerAuthId() {
	return ownerAuthId;
 }
 public void setOwnerAuthId(Long ownerAuthId) {
	this.ownerAuthId = ownerAuthId;
 }
 public String getCode() {
	return code;
 }
 public void setCode(String code) {
	this.code = code;
 }
 public String getFileName() {
	return fileName;
 }
 public void setFileName(String fileName) {
	this.fileName = fileName;
 }
}