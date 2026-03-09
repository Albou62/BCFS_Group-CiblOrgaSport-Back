package com.ciblorgasport.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciblorgasport.user_service.entity.VolontaireProfile;

public interface VolontaireRepository extends JpaRepository<VolontaireProfile, Long> {
    // Utiliser Optional évite les NullPointerException
    // IgnoreCase évite les erreurs si Adama s'écrit "adama" ou "Adama"
    Optional<VolontaireProfile> findFirstByUsernameIgnoreCase(String username);

    Optional<VolontaireProfile> findByAuthUserId(Long authUserId);
}