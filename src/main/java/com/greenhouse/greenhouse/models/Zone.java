package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Flowerpot> flowerpots;

    @ManyToOne
    @JoinColumn(name = "greenhouse_id")
    @JsonBackReference
    private Greenhouse greenhouse;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.MERGE)
    @JsonManagedReference("zone-params")
    private List<ParameterEntity> parameters;

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

    public void addParameter (ParameterEntity parameterEntity) {
        this.parameters.add(parameterEntity);
    }

    public void deleteParameter (String name) {
        this.parameters.removeIf(parameterEntity -> parameterEntity.getName().equals(name));
    }

    public List<ParameterEntity> getParameters () {
        return this.parameters;
    }

    public void setParameters (List<ParameterEntity> parameters) {
        this.parameters = parameters;
    }

    public Greenhouse getGreenhouse () {
        return greenhouse;
    }

    public void setGreenhouse (Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }
}
