package com.tirana.smartparking.user.service;

import com.tirana.smartparking.user.dto.CarCreateDTO;
import com.tirana.smartparking.user.dto.CarResponseDTO;
import com.tirana.smartparking.user.dto.UserCarsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface CarService {
    Page<CarResponseDTO> getAllCars(Pageable pageable);
    CarResponseDTO createCar(CarCreateDTO carCreateDTO);
    CarResponseDTO findById(Long id);
    UserCarsDTO updateCar(Long id, CarCreateDTO carUpdateDTO);
    UserCarsDTO patchCar(Long id, CarCreateDTO carUpdateDTO);
    void deleteCar(Long id);
}
