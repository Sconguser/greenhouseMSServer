package com.greenhouse.greenhouse.responses;

public class PlantResponse {
    private Long id;
    private String name;
    private String description;
    private int minTemperature;

    private int maxTemperature;

    private int minHumidity;

    private int maxHumidity;
    private String image_data_base64;

    public PlantResponse (Long id, String name, String description, int minTemperature, int maxTemperature,
                          int minHumidity, int maxHumidity, String image_data_base64)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.image_data_base64 = image_data_base64;
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

    public String getImage_data_base64() {
        return image_data_base64;
    }

    public void setImage_data_base64(String image_data_base64) {
        this.image_data_base64 = image_data_base64;
    }

    public int getMinTemperature () {
        return minTemperature;
    }

    public void setMinTemperature (int minTemperature) {
        this.minTemperature = minTemperature;
    }
}
