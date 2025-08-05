package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<RequirementEntity> requirements = new ArrayList<>();

    @Lob
    @Column
    private byte[] imageData;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "plants")
    private List<Flowerpot> flowerPots;

    public Plant () {
    }

    public List<RequirementEntity> getRequirements () {
        return requirements;
    }

    public void addRequirement (RequirementEntity requirement) {
        requirement.setPlant(this);
        this.requirements.add(requirement);
    }

    public void removeRequirement (String name) {
        requirements.removeIf(entity -> entity.getName()
                .equals(name));
    }


    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
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

    @Override
    public String toString () {
        return "Plant: " + name + "\n";
    }
}
