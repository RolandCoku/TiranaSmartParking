package com.tirana.smartparking.user.repository;

import com.tirana.smartparking.user.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    Page<Car> findByUserId(Long userId, Pageable pageable);
}
