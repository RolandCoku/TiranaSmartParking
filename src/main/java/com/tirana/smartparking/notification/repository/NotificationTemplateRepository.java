package com.tirana.smartparking.notification.repository;

import com.tirana.smartparking.notification.entity.NotificationTemplate;
import com.tirana.smartparking.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    
    Optional<NotificationTemplate> findByTemplateId(String templateId);
    
    List<NotificationTemplate> findByType(Notification.NotificationType type);
    
    List<NotificationTemplate> findByIsActiveTrue();
    
    List<NotificationTemplate> findByTypeAndIsActiveTrue(Notification.NotificationType type);
    
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateId = :templateId AND nt.isActive = true")
    Optional<NotificationTemplate> findActiveByTemplateId(@Param("templateId") String templateId);
    
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.type = :type AND nt.isActive = true")
    List<NotificationTemplate> findActiveByType(@Param("type") Notification.NotificationType type);
}
