package com.example.taxi3;

public class Car {
    private int id;
    private String brand;
    private String model;
    private int year;
    private int speed;
    private double price;

    public Car(int id, String brand, String model, int year, int speed, double price) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.speed = speed;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public int getYear() {
        return year;
    }

    public int getSpeed() {
        return speed;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Brand: " + brand + ", Model: " + model +
                ", Year: " + year + ", Speed: " + speed + ", Price: " + price;
    }

}
