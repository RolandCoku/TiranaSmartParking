package com.tirana.smartparking.notification.repository;

import com.tirana.smartparking.notification.entity.NotificationPreferences;
import com.tirana.smartparking.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    
    Optional<NotificationPreferences> findByUser(User user);
    
    Optional<NotificationPreferences> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
