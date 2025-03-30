package com.greenhouse.greenhouse.responses;

public class PlantResponse {
    private Long id;
    private String name;
    private String description;

    private int minTemperature;
    private int maxTemperature;

    private int minHumidity;
    private int maxHumidity;

    private int minSoilHumidity;

    private int maxSoilHumidity;
    private byte[] imageData;

    public PlantResponse (Long id, String name, String description, int minTemperature, int maxTemperature,
                          int minHumidity, int maxHumidity, byte[] imageData)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.imageData = imageData;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxTemperature () {
        return maxTemperature;
    }

    public void setMaxTemperature (int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMinHumidity () {
        return minHumidity;
    }

    public void setMinHumidity (int minHumidity) {
        this.minHumidity = minHumidity;
    }

    public int getMaxHumidity () {
        return maxHumidity;
    }

    public void setMaxHumidity (int maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public byte[] getImageData () {
        return imageData;
    }

    public void setImageData (byte[] imageData) {
        this.imageData = imageData;
    }

    public int getMinTemperature () {
        return minTemperature;
    }

    public void setMinTemperature (int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMinSoilHumidity () {
        return minSoilHumidity;
    }

    public void setMinSoilHumidity (int minSoilHumidity) {
        this.minSoilHumidity = minSoilHumidity;
    }

    public int getMaxSoilHumidity () {
        return maxSoilHumidity;
    }

    public void setMaxSoilHumidity (int maxSoilHumidity) {
        this.maxSoilHumidity = maxSoilHumidity;
    }
}
