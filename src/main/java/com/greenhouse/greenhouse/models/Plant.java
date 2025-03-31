package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    private int minTemperature;
    private int maxTemperature;

    private int minHumidity;
    private int maxHumidity;

    private int minSoilHumidity;
    private int maxSoilHumidity;

    @Lob
    @Column
    private byte[] imageData;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "plants")
    private List<Greenhouse> greenhouses;

    public Plant () {
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public int getMinTemperature () {
        return minTemperature;
    }

    public void setMinTemperature (int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMinHumidity () {
        return minHumidity;
    }

    public void setMinHumidity (int minHumidity) {
        this.minHumidity = minHumidity;
    }

    public int getMaxSoilHumidity () {
        return maxSoilHumidity;
    }

    public void setMaxSoilHumidity (int maxSoilHumidity) {
        this.maxSoilHumidity = maxSoilHumidity;
    }

    public int getMinSoilHumidity () {
        return minSoilHumidity;
    }

    public void setMinSoilHumidity (int minSoilHumidity) {
        this.minSoilHumidity = minSoilHumidity;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public byte[] getImageData () {
        return imageData;
    }

    public void setImageData (byte[] imageData) {
        this.imageData = imageData;
    }

    public int getMaxTemperature () {
        return maxTemperature;
    }

    public void setMaxTemperature (int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMaxHumidity () {
        return maxHumidity;
    }

    public void setMaxHumidity (int maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    @Override
    public String toString () {
        return "Plant: " + name + "\n" + "required min / max temperature " + minTemperature + " / " + maxTemperature + "\n" + "required min / max humidity " + minHumidity + " / " + maxHumidity + "\n";
    }
}
