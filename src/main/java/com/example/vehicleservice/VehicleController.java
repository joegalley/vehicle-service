package com.example.vehicleservice;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class VehicleController {

    private final Logger logger = LogManager.getLogger(VehicleController.class);

    private final String inventoryBaseUrl;

    private static final String LOOKUP_STR = "Looking up vehicle info";
    private static final String RETURNING_STR = "Returning vehicle info to caller";

    private static final String DB_ERR = "Error connecting to database";
    private static final String GATEWAY_ERR = "Error connecting to gateway";

    private final RestTemplate restTemplate;

    @Autowired
    public VehicleController(@Value("${service.inventory.baseUrl}") String inventoryBaseUrl,
                             RestTemplate restTemplate) {
        this.inventoryBaseUrl = inventoryBaseUrl;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<Object> getVehicles() {
        logger.info(LOOKUP_STR);

        List<Vehicle> vehicles = getVehicleInfo();

        logger.info(RETURNING_STR);

        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<Object> getVehicle(@PathVariable String vehicleId) {
        logger.info(LOOKUP_STR);

        List<Vehicle> vehicles = getVehicleInfo();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equalsIgnoreCase(vehicleId)) {
                return new ResponseEntity<>(vehicle, HttpStatus.OK);
            }
        }

        logger.info(RETURNING_STR);

        return new ResponseEntity<>("Vehicle with ID " + vehicleId + " not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/vehicles", params = "delay")
    public ResponseEntity<Object> getVehiclesWithDelay(@RequestParam("delay") int delay) throws InterruptedException {
        logger.info(LOOKUP_STR);

        Thread.sleep(delay * 1000L);

        List<Vehicle> vehicles = getVehicleInfo();

        logger.info(RETURNING_STR);

        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping(value = "/vehicles", params = "error")
    public ResponseEntity<Object> getVehiclesWithDelay(@RequestParam("error") String error) throws InterruptedException {
        logger.info(LOOKUP_STR);

        if (error.equalsIgnoreCase("db")) {
            logger.error(DB_ERR);
            return new ResponseEntity<>(DB_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (error.equalsIgnoreCase("gateway")) {
            logger.error(GATEWAY_ERR);
            return new ResponseEntity<>(GATEWAY_ERR, HttpStatus.BAD_GATEWAY);
        }

        List<Vehicle> vehicles = getVehicleInfo();

        logger.info(RETURNING_STR);

        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    private Integer getInStockCount(String vehicleId) {
        String url = inventoryBaseUrl + "/" + vehicleId;

        try {
            final JsonNode responseBody = restTemplate.getForObject(url, JsonNode.class);
            return responseBody.get("inStockCount").asInt();
        } catch (HttpClientErrorException e) {
            logger.error("Exception looking up the inventory count for vehicle " + vehicleId, e);
        }

        return 0;
    }

    private List<Vehicle> getVehicleInfo() {
        List<Vehicle> vehicles = new ArrayList<>();

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleId("vehicle-1");
        vehicle1.setDescription("Toyota Camry");
        vehicle1.setMinCreditScore(500);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleId("vehicle-2");
        vehicle2.setDescription("Honda Accord");
        vehicle2.setMinCreditScore(575);

        Vehicle vehicle3 = new Vehicle();
        vehicle3.setVehicleId("vehicle-3");
        vehicle3.setDescription("Nissan Rogue");
        vehicle3.setMinCreditScore(650);

        Vehicle vehicle4 = new Vehicle();
        vehicle4.setVehicleId("vehicle-4");
        vehicle4.setDescription("Tesla Model S");
        vehicle4.setMinCreditScore(800);

        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        vehicles.add(vehicle3);
        vehicles.add(vehicle4);

        for (Vehicle vehicle : vehicles) {
            vehicle.setInStockCount(getInStockCount(vehicle.getVehicleId()));
        }

        return vehicles;
    }
}