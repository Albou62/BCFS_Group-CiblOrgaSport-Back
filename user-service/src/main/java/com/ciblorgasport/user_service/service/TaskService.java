package com.ciblorgasport.user_service.service;

import com.ciblorgasport.user_service.entity.Task;
import com.ciblorgasport.user_service.entity.VolontaireProfile;
import com.ciblorgasport.user_service.repository.TaskRepository;
import com.ciblorgasport.user_service.repository.VolontaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    @Autowired private TaskRepository taskRepo;
    @Autowired private VolontaireRepository volontaireRepo;

    public Task assignTask(Long userId, String username, String title, String timeSlot) {
        // On cherche par authUserId
        VolontaireProfile vol = volontaireRepo.findByAuthUserId(userId)
                .orElseGet(() -> {
                    VolontaireProfile newProfile = new VolontaireProfile();
                    newProfile.setAuthUserId(userId);
                    newProfile.setUsername(username); // Indispensable pour la vue Volontaire !
                    return volontaireRepo.save(newProfile);
                });

        Task task = new Task();
        task.setTitle(title);
        task.setTimeSlot(timeSlot);
        task.setStatus("À venir");
        task.setVolontaire(vol);
        return taskRepo.save(task);
    }
    public List<Task> getTasksForVolunteer(String username) {
        if (username == null || username.isEmpty()) return List.of();
        
        // On utilise la méthode de recherche par traversée de relation
        // C'est beaucoup plus robuste car ça ne dépend pas de l'ID du profil en mémoire
        return taskRepo.findByVolontaireUsername(username);
    }

    public List<VolontaireProfile> getAllVolunteers() {
        return volontaireRepo.findAll();
    }
}