package com.greenhouse.greenhouse.requests;

public class PlantRequest {
    private String name;
    private String description;

    private Integer minTemperature;
    private Integer maxTemperature;

    private Integer minHumidity;
    private Integer maxHumidity;

    private Integer minSoilHumidity;

    private Integer maxSoilHumidity;

    private String image_data_base64;

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

    public Integer getMinTemperature () {
        return minTemperature;
    }

    public void setMinTemperature (int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Integer getMaxTemperature () {
        return maxTemperature;
    }

    public void setMaxTemperature (int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Integer getMinHumidity () {
        return minHumidity;
    }

    public void setMinHumidity (int minHumidity) {
        this.minHumidity = minHumidity;
    }

    public Integer getMaxHumidity () {
        return maxHumidity;
    }

    public void setMaxHumidity (int maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Integer getMaxSoilHumidity () {
        return maxSoilHumidity;
    }

    public void setMaxSoilHumidity (Integer maxSoilHumidity) {
        this.maxSoilHumidity = maxSoilHumidity;
    }

    public Integer getMinSoilHumidity () {
        return minSoilHumidity;
    }

    public void setMinSoilHumidity (Integer minSoilHumidity) {
        this.minSoilHumidity = minSoilHumidity;
    }

    public String getImage_data() {
        return image_data_base64;
    }

    public void setImage_data(String image_data_base64) {
        this.image_data_base64 = image_data_base64;
    }
}
