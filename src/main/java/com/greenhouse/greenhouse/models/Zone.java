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
    private List<ParameterEntity> requestedParameters;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<ParameterEntity> currentParameters;

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

    public void addRequestedParameter (ParameterEntity parameterEntity) {
        parameterEntity.setZone(this);
        this.requestedParameters.add(parameterEntity);
    }

    public void deleteRequestedParameter (String name) {
        this.requestedParameters.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getRequestedParameters () {
        return this.requestedParameters;
    }

    public void setRequestedParameters (List<ParameterEntity> requestedParameters) {
        this.requestedParameters = requestedParameters;
    }

    public void addCurrentParameter (ParameterEntity currentParameter) {
        currentParameter.setZone(this);
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

    public Greenhouse getGreenhouse () {
        return greenhouse;
    }

    public void setGreenhouse (Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }
}
