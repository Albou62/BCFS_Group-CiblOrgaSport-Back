package com.ciblorgasport.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciblorgasport.user_service.entity.VolontaireProfile;

public interface VolontaireRepository extends JpaRepository<VolontaireProfile, Long> {
    Optional<VolontaireProfile> findFirstByUsernameIgnoreCase(String username);

    Optional<VolontaireProfile> findByAuthUserId(Long authUserId);
}