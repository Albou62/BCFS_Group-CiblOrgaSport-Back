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
        VolontaireProfile vol = volontaireRepo.findByAuthUserId(userId)
                .orElseGet(() -> {
                    VolontaireProfile newProfile = new VolontaireProfile();
                    newProfile.setAuthUserId(userId);
                    newProfile.setUsername(username); 
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
        
        return taskRepo.findByVolontaireUsername(username);
    }

    public List<VolontaireProfile> getAllVolunteers() {
        return volontaireRepo.findAll();
    }
}