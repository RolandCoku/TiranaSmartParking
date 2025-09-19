package com.tirana.smartparking.notification.repository;

import com.tirana.smartparking.notification.entity.NotificationRecipient;
import com.tirana.smartparking.notification.entity.Notification;
import com.tirana.smartparking.user.entity.User;
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
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    
    List<NotificationRecipient> findByUser(User user);
    
    List<NotificationRecipient> findByNotificationId(Long notificationId);
    
    List<NotificationRecipient> findByStatus(NotificationRecipient.DeliveryStatus status);
    
    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.user.id = :userId ORDER BY nr.createdAt DESC")
    Page<NotificationRecipient> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, 
                                                                 Pageable pageable);
    
    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.user.id = :userId AND nr.status = :status ORDER BY nr.createdAt DESC")
    Page<NotificationRecipient> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId, 
                                                                           @Param("status") NotificationRecipient.DeliveryStatus status, 
                                                                           Pageable pageable);
    
    @Query("SELECT COUNT(nr) FROM NotificationRecipient nr WHERE nr.user.id = :userId AND nr.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, 
                                @Param("status") NotificationRecipient.DeliveryStatus status);
    
    @Query("SELECT COUNT(nr) FROM NotificationRecipient nr WHERE nr.user.id = :userId AND nr.readAt IS NULL")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.user.id = :userId AND nr.readAt IS NULL ORDER BY nr.createdAt DESC")
    List<NotificationRecipient> findUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.status = :status AND nr.retryCount < :maxRetries")
    List<NotificationRecipient> findFailedNotificationsForRetry(@Param("status") NotificationRecipient.DeliveryStatus status, 
                                                                 @Param("maxRetries") Integer maxRetries);
    
    Optional<NotificationRecipient> findByUserAndNotificationIdAndChannel(User user, Long notificationId, 
                                                                           Notification.NotificationChannel channel);
    
    @Query("UPDATE NotificationRecipient nr SET nr.readAt = :readAt WHERE nr.user.id = :userId AND nr.readAt IS NULL")
    void markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") Instant readAt);
}
