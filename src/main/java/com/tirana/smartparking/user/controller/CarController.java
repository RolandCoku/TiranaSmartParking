package com.tirana.smartparking.user.controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    // This controller will handle car-related operations,
    // For example, adding a car, deleting a car, getting all cars, etc.

    @GetMapping
    public String getAllCars() {
        // This method would typically return a list of all cars
        return "List of all cars";
    }

    @PostMapping
    public String addCar() {
        // This method would typically add a new car to the system
        // Needs a way to get the user ID or context to associate the car with a user
        return "Car added!";
    }

    @PutMapping("/{id}")
    public String updateCar(@PathVariable Long id) {
        // This method would typically update a car's information
        return "Car with ID: " + id + " updated!";
    }

    @PatchMapping("/{id}")
    public String patchCar(@PathVariable Long id) {
        // This method would typically partially update a car's information
        return "Car with ID: " + id + " patched!";
    }

    @DeleteMapping("/{id}")
    public  String deleteCar(@PathVariable Long id) {
        // This method would typically delete a car from the system
        return "Car with ID: " + id + " deleted!";
    }

}
