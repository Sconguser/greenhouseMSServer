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
    private List<ParameterEntity> parameterEntities;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Flowerpot () {

    }

    public List<ParameterEntity> getParameterEntities () {
        return parameterEntities;
    }

    public void addParameterEntity (ParameterEntity parameterEntity) {
        parameterEntity.setFlowerpot(this);
        parameterEntities.add(parameterEntity);
    }

    public void removeParameterEntity (String name) {
        parameterEntities.removeIf(entity -> entity.name.equals(name));
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
