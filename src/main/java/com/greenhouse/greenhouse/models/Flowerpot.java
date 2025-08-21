package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Flowerpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(name = "flowerpot_plant", joinColumns = @JoinColumn(name = "flowerpot_id"), inverseJoinColumns = @JoinColumn(name = "plant_id"))
    private List<Plant> plants;

    @OneToMany(mappedBy = "flowerpot", cascade = CascadeType.MERGE)
    @JsonManagedReference("flowerpot-params")
    private List<ParameterEntity> parameters;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    @JsonBackReference
    private Zone zone;

    public Flowerpot () {

    }

    public void addParameter (ParameterEntity parameter) {
        this.parameters.add(parameter);
    }

    public void deleteParameter (String name) {
        this.parameters.removeIf(parameterEntity -> parameterEntity.getName()
                .equals(name));
    }

    public List<ParameterEntity> getParameters () {
        return this.parameters;
    }

    public void setParameters (List<ParameterEntity> parameters) {
        this.parameters = parameters;
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

    public List<Plant> getPlants () {
        return plants;
    }

    public void setPlants (List<Plant> plants) {
        this.plants = plants;
    }

    public void addPlant (Plant plant) {
        this.plants.add(plant);
    }

    public void deletePlant (Long plantId) {
        this.plants.removeIf(plant -> plant.getId()
                .equals(plantId));
    }

    @Override
    public String toString () {
        return "Flowerpot: " + this.name + "\n";
    }


    public Zone getZone () {
        return zone;
    }

    public void setZone (Zone zone) {
        this.zone = zone;
    }
}
