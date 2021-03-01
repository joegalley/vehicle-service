package com.example.vehicleservice;

public class Vehicle {

    private String vehicleId;
    private String description;
    private Integer inStockCount;

    public String getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(Integer inStockCount) {
        this.inStockCount = inStockCount;
    }
}