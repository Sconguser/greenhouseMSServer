package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<Flowerpot> flowerpots;

    @ManyToOne
    @JoinColumn(name = "greenhouse_id")
    private Greenhouse greenhouse;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<ParameterEntity> parameterEntities;

    public Zone () {
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

    public List<Flowerpot> getFlowerpots () {
        return flowerpots;
    }

    public void setFlowerpots (List<Flowerpot> flowerpots) {
        this.flowerpots = flowerpots;
    }

    public void addFlowerpot (Flowerpot flowerpot) {
        this.flowerpots.add(flowerpot);
    }

    public void deleteFlowerpot (Long flowerpotId) {
        this.flowerpots.removeIf(flowerpot -> flowerpot.getId()
                .equals(flowerpotId));
    }

    public void addParameterEntity (ParameterEntity parameterEntity) {
        parameterEntity.setZone(this);
        this.parameterEntities.add(parameterEntity);
    }

    public void deleteParameterEntity (String name) {
        this.parameterEntities.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getParameterEntities () {
        return this.parameterEntities;
    }

    public Greenhouse getGreenhouse () {
        return greenhouse;
    }

    public void setGreenhouse (Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public void setParameterEntities (List<ParameterEntity> parameterEntities) {
        this.parameterEntities = parameterEntities;
    }
}
