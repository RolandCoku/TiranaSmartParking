package com.tirana.smartparking.notification.repository;

import com.tirana.smartparking.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Optional<Notification> findByNotificationId(String notificationId);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.scheduledAt <= :now")
    List<Notification> findScheduledNotifications(@Param("status") Notification.NotificationStatus status, 
                                                  @Param("now") Instant now);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status ORDER BY n.createdAt ASC")
    Page<Notification> findByStatusOrderByCreatedAt(@Param("status") Notification.NotificationStatus status, 
                                                    Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    Long countByStatus(Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.status = :status")
    List<Notification> findByTypeAndStatus(@Param("type") Notification.NotificationType type, 
                                           @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :fromDate AND n.createdAt <= :toDate")
    Page<Notification> findByCreatedAtBetween(@Param("fromDate") Instant fromDate, 
                                              @Param("toDate") Instant toDate, 
                                              Pageable pageable);
}
