package com.greenhouse.greenhouse.models;

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

    @OneToMany(mappedBy = "flowerpot", cascade = CascadeType.ALL)
    private List<ParameterEntity> requestedParameters;

    @OneToMany(mappedBy = "flowerpot", cascade = CascadeType.ALL)
    private List<ParameterEntity> currentParameters;


    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Flowerpot () {

    }

    public List<ParameterEntity> getRequestedParameters () {
        return requestedParameters;
    }

    public void addRequestedParameter (ParameterEntity parameterEntity) {
        parameterEntity.setFlowerpot(this);
        requestedParameters.add(parameterEntity);
    }

    public void removeRequestedParameter (String name) {
        requestedParameters.removeIf(entity -> entity.name.equals(name));
    }

    public void addCurrentParameter (ParameterEntity currentParameter) {
        currentParameter.setFlowerpot(this);
        this.currentParameters.add(currentParameter);
    }

    public void deleteCurrentParameter (String name) {
        this.currentParameters.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getCurrentParameters () {
        return this.currentParameters;
    }

    public void setCurrentParameters (List<ParameterEntity> currentParameters) {
        this.currentParameters = currentParameters;
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
