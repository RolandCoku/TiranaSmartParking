package com.tirana.smartparking.user.repository;

import com.tirana.smartparking.user.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    @EntityGraph(attributePaths = "user")
    @NonNull
    Page<Car> findAll(@NonNull Pageable pageable);
    Page<Car> findByUserId(Long userId, Pageable pageable);
    boolean existsByLicensePlate(String plateNumber);
    boolean existsByLicensePlateAndIdNot(String plateNumber, Long id);

    @Query("SELECT c FROM Car c WHERE c.user.id = :userId AND c.id = :carId")
    Optional<Car> findBuUserIdAndCarId(Long userId, Long carId);
}
