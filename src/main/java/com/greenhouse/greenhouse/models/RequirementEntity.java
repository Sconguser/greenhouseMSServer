package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class RequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    public Plant getPlant () {
        return this.plant;
    }

    public void setPlant (Plant plant) {
        this.plant = plant;
    }
}
