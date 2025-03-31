package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
public class Greenhouse {
    @ManyToMany
    @JoinTable(name = "greenhouse_plant", joinColumns = @JoinColumn(name = "greenhouse_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    List<Plant> plants;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String ipAddress;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", unique = true)
    private GreenhouseStatus status;

    public Greenhouse () {
        status = new GreenhouseStatus();
        this.status.setGreenhouse(this);
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getLocation () {
        return location;
    }

    public void setLocation (String location) {
        this.location = location;
    }

    public String getIpAddress () {
        return ipAddress;
    }

    public void setIpAddress (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<Plant> getPlants () {
        return plants;
    }

    public void setPlants (List<Plant> plants) {
        this.plants = plants;
    }

    public GreenhouseStatus getStatus () {
        return status;
    }

    public void setStatus (GreenhouseStatus status) {
        this.status = status;
        status.setGreenhouse(this);
    }

    @Override
    public String toString () {
        return "Greenhouse: " + this.name + "\n" + "location " + this.location + "\n";
    }
}
