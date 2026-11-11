package com.mca.hallbooking.model;

public class Hall {
    private long id;
    private String name;
    private String location;
    private int capacity;
    private String amenities;
    private double price;
    private String description;
    private String imageUris;

    public Hall() {
    }

    public Hall(long id, String name, String location, int capacity, String amenities, double price, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.amenities = amenities;
        this.price = price;
        this.description = description;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUris() { return imageUris; }
    public void setImageUris(String imageUris) { this.imageUris = imageUris; }
}
