package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Zone> zones = new ArrayList<>();

    private String name;
    private String location;
    private String ipAddress;

    private Status status = Status.OFF;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "last_pushed")
    private LocalDateTime lastPushed;

    @Column(name = "device_config", columnDefinition = "TEXT")
    private String deviceConfigJson;

    @Column(name = "mapping_config", columnDefinition = "TEXT")
    private String mappingConfigJson;

    @Column(name = "device_config_synced")
    private Boolean deviceConfigSynced = null;  // null=never pushed, false=pending ACK, true=synced

    @Column(name = "mapping_config_synced")
    private Boolean mappingConfigSynced = null;

    @Column(name = "model_synced")
    private Boolean modelSynced = null;

    @Column(name = "model_dirty_at")
    private LocalDateTime modelDirtyAt;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    @JsonManagedReference("greenhouse-params")
    private final List<ParameterEntity> parameters = new ArrayList<>();

    public Greenhouse () {
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

    @Override
    public String toString () {
        return "Greenhouse: " + this.name + "\n" + "location " + this.location + "\n";
    }

    public List<Zone> getZones () {
        return zones;
    }

    public void setZones (List<Zone> zones) {
        this.zones = zones;
    }

    public void addZone (Zone zone) {
        this.zones.add(zone);
    }

    @JsonIgnore
    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public void addParameter (ParameterEntity parameterEntity) {
        this.parameters.add(parameterEntity);
    }

    public void removeParameter (String name) {
        this.parameters.removeIf(parameterEntity -> parameterEntity.getName()
                .equals(name));
    }

    public List<ParameterEntity> getParameters () {
        return this.parameters;
    }

    public void removeZone (Long zoneId) {
        this.zones.removeIf(zone -> zone.getId()
                .equals(zoneId));
    }


    @JsonIgnore
    public LocalDateTime getLastUpdate () {
        return lastUpdate;
    }

    public void setLastUpdate (LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @JsonIgnore
    public LocalDateTime getLastPushed () {
        return lastPushed;
    }

    public void setLastPushed (LocalDateTime lastPushed) {
        this.lastPushed = lastPushed;
    }

    @JsonIgnore
    public String getDeviceConfigJson () { return deviceConfigJson; }
    public void setDeviceConfigJson (String deviceConfigJson) { this.deviceConfigJson = deviceConfigJson; }

    @JsonIgnore
    public String getMappingConfigJson () { return mappingConfigJson; }
    public void setMappingConfigJson (String mappingConfigJson) { this.mappingConfigJson = mappingConfigJson; }

    @JsonIgnore
    public Boolean getDeviceConfigSynced () { return deviceConfigSynced; }
    public void setDeviceConfigSynced (Boolean deviceConfigSynced) { this.deviceConfigSynced = deviceConfigSynced; }

    @JsonIgnore
    public Boolean getMappingConfigSynced () { return mappingConfigSynced; }
    public void setMappingConfigSynced (Boolean mappingConfigSynced) { this.mappingConfigSynced = mappingConfigSynced; }

    @JsonIgnore
    public Boolean getModelSynced () { return modelSynced; }
    public void setModelSynced (Boolean modelSynced) { this.modelSynced = modelSynced; }

    @JsonIgnore
    public LocalDateTime getModelDirtyAt () { return modelDirtyAt; }
    public void setModelDirtyAt (LocalDateTime modelDirtyAt) { this.modelDirtyAt = modelDirtyAt; }
}
