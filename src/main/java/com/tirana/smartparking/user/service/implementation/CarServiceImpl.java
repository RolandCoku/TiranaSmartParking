package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.user.dto.CarCreateDTO;
import com.tirana.smartparking.user.dto.CarResponseDTO;
import com.tirana.smartparking.user.dto.UserCarsDTO;
import com.tirana.smartparking.user.entity.Car;
import com.tirana.smartparking.user.repository.CarRepository;
import com.tirana.smartparking.user.service.CarService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Page<CarResponseDTO> getAllCars(Pageable pageable) {
        Page<Car> cars = carRepository.findAll(pageable);

        return cars.map(car -> new CarResponseDTO(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getColor(),
                car.getUser() != null ? car.getUser().getId() : null,
                car.getUser() != null ? car.getUser().getFirstName() : null,
                car.getUser() != null ? car.getUser().getLastName() : null
        ));
    }

    //TODO: Will implement this method later after I have set up authentication and authorization so that I can associate the car with the user
    @Override
    public CarResponseDTO createCar(CarCreateDTO carCreateDTO) {
        return new CarResponseDTO();
    }

    //TODO: This will have to check if the request is authorized to access the car
    @Override
    @Transactional(rollbackOn = Exception.class)
    public CarResponseDTO findById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car with ID " + id + " not found"));

        return getCarResponseDTO(car);
    }

    //TODO: The request will have to be authorized to update the car
    @Override
    public UserCarsDTO updateCar(Long id, CarCreateDTO carUpdateDTO) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car with ID " + id + " not found"));

        // Update the car properties

        // Check if the license plate already exists
        if (carRepository.existsByLicensePlateAndIdNot(carUpdateDTO.getLicensePlate(), id)) {
            throw new IllegalArgumentException("License plate " + carUpdateDTO.getLicensePlate() + " already exists");
        }
        car.setLicensePlate(carUpdateDTO.getLicensePlate());
        car.setBrand(carUpdateDTO.getBrand());
        car.setModel(carUpdateDTO.getModel());
        car.setColor(carUpdateDTO.getColor());

        // Save the updated car
        car = carRepository.save(car);

        return new UserCarsDTO(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getColor(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }

    @Override
    public UserCarsDTO patchCar(Long id, CarCreateDTO carUpdateDTO) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car with ID " + id + " not found"));

        // Update only the fields that are present in the DTO
        if (carUpdateDTO.getLicensePlate() != null) {
            // Check if the license plate already exists, except for the current car
            if (carRepository.existsByLicensePlateAndIdNot(carUpdateDTO.getLicensePlate(), id)) {
                throw new IllegalArgumentException("License plate " + carUpdateDTO.getLicensePlate() + " already exists");
            }
            car.setLicensePlate(carUpdateDTO.getLicensePlate());
        }
        if (carUpdateDTO.getBrand() != null) {
            car.setBrand(carUpdateDTO.getBrand());
        }
        if (carUpdateDTO.getModel() != null) {
            car.setModel(carUpdateDTO.getModel());
        }
        if (carUpdateDTO.getColor() != null) {
            car.setColor(carUpdateDTO.getColor());
        }

        // Save the updated car
        car = carRepository.save(car);

        return new UserCarsDTO(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getColor(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car with ID " + id + " not found"));

        // Delete the car
        carRepository.delete(car);
    }

    @Override
    public UserCarsDTO getCarByUserAndId(Long userId, Long carId) {
        Car car = carRepository.findBuUserIdAndCarId(userId, carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car with ID " + carId + " not found!"));

        return new UserCarsDTO(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getColor(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }

    private CarResponseDTO getCarResponseDTO(Car car) {
        return new CarResponseDTO(
                car.getId(),
                car.getLicensePlate(),
                car.getBrand(),
                car.getModel(),
                car.getColor(),
                car.getUser() != null ? car.getUser().getId() : null,
                car.getUser() != null ? car.getUser().getFirstName() : null,
                car.getUser() != null ? car.getUser().getLastName() : null
        );
    }
}
