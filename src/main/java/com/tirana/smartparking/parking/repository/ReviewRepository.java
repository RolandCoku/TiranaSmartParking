package com.tirana.smartparking.parking.repository;

import com.tirana.smartparking.parking.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByParkingLotId(Long parkingLotId, Pageable pageable);
}
