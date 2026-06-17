package com.greenhouse.greenhouse.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.*;
import com.greenhouse.greenhouse.mappers.GreenhouseMapper;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.mappers.ZoneMapper;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
import com.greenhouse.greenhouse.services.AnalyticsService;
import com.greenhouse.greenhouse.services.ConfigService;
import com.greenhouse.greenhouse.services.GreenhouseService;
import com.greenhouse.greenhouse.services.MqttPublisher;
import com.greenhouse.greenhouse.services.PlantHealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GreenhouseService.updateTelemetry().
 *
 * Simulates the JSON the Arduino publishes to greenhouse/{IP}/status:
 *   { "id": <gh_id>, "zones": [{ "id": <z_id>, "parameters": [{ "id": <p_id>, "val": <float> }] }] }
 */
@ExtendWith(MockitoExtension.class)
class TelemetryUpdateServiceTest {

    @Mock GreenhouseRepository greenhouseRepository;
    @Mock GreenhouseMapper greenhouseMapper;
    @Mock ZoneMapper zoneMapper;
    @Mock ZoneRepository zoneRepository;
    @Mock ParameterMapper parameterMapper;
    @Mock MqttPublisher mqttPublisher;
    @Mock ObjectMapper objectMapper;
    @Mock ConfigService configService;
    @Mock AnalyticsService analyticsService;
    @Mock PlantHealthService plantHealthService;

    GreenhouseService greenhouseService;

    // Reusable test data
    Greenhouse greenhouse;
    Zone zone;
    Flowerpot flowerpot;
    ParameterEntity zoneParam;
    ParameterEntity flowerpotParam;

    @BeforeEach
    void setUp() {
        greenhouseService = new GreenhouseService(
            greenhouseRepository, greenhouseMapper, zoneMapper,
            zoneRepository, parameterMapper, mqttPublisher, objectMapper, configService,
            analyticsService, plantHealthService
        );

        zoneParam = new ParameterEntity();
        zoneParam.setId(5L);
        zoneParam.setCurrentValue(20.0);

        flowerpotParam = new ParameterEntity();
        flowerpotParam.setId(10L);
        flowerpotParam.setCurrentValue(40.0);

        flowerpot = new Flowerpot();
        flowerpot.setId(1L);
        flowerpot.setParameters(List.of(flowerpotParam));

        zone = new Zone();
        zone.setId(1L);
        zone.setParameters(List.of(zoneParam));
        zone.setFlowerpots(List.of(flowerpot));

        greenhouse = new Greenhouse();
        greenhouse.setId(1L);
        greenhouse.setIpAddress("192.168.1.100");
        greenhouse.setStatus(Status.ON);
        greenhouse.setZones(List.of(zone));
    }

    // -------------------------------------------------------------------------
    // currentValue updates
    // -------------------------------------------------------------------------

    @Test
    void updateTelemetry_updatesCurrentValueOfZoneParameter() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 27.3, null));

        assertEquals(27.3, zoneParam.getCurrentValue(), 0.001);
    }

    @Test
    void updateTelemetry_updatesCurrentValueOfFlowerpotParameter() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        TelemetryGreenhouseDTO telemetry = new TelemetryGreenhouseDTO();
        telemetry.id = 1L;

        TelemetryFlowerpotDTO fpDto = new TelemetryFlowerpotDTO();
        fpDto.id = 1L;
        TelemetryParameterDTO pDto = new TelemetryParameterDTO();
        pDto.id = 10L;
        pDto.val = 65.0;
        fpDto.parameters = List.of(pDto);

        TelemetryZoneDTO zDto = new TelemetryZoneDTO();
        zDto.id = 1L;
        zDto.parameters = new ArrayList<>();
        zDto.flowerpots = List.of(fpDto);
        telemetry.zones = List.of(zDto);

        greenhouseService.updateTelemetry(telemetry);

        assertEquals(65.0, flowerpotParam.getCurrentValue(), 0.001);
    }

    @Test
    void updateTelemetry_doesNotUpdateCurrentValueWhenValIsNull() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        TelemetryGreenhouseDTO telemetry = buildTelemetry(5L, null, null);
        greenhouseService.updateTelemetry(telemetry);

        assertEquals(20.0, zoneParam.getCurrentValue(), "val=null should leave currentValue unchanged");
    }

    @Test
    void updateTelemetry_ignoresUnknownParameterId() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        // Send telemetry with a parameter ID that doesn't exist in the model
        greenhouseService.updateTelemetry(buildTelemetry(999L, 50.0, null));

        assertEquals(20.0, zoneParam.getCurrentValue(), "Unknown parameter ID must not affect existing values");
    }

    // -------------------------------------------------------------------------
    // Status management
    // -------------------------------------------------------------------------

    @Test
    void updateTelemetry_setsStatusToOn_whenGreenhouseWasNotResponsive() {
        greenhouse.setStatus(Status.NOT_RESPONSIVE);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        assertEquals(Status.ON, greenhouse.getStatus());
    }

    @Test
    void updateTelemetry_setsStatusToOn_whenGreenhouseWasOff() {
        greenhouse.setStatus(Status.OFF);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        assertEquals(Status.ON, greenhouse.getStatus());
    }

    @Test
    void updateTelemetry_keepsStatusOn_whenAlreadyOn() {
        greenhouse.setStatus(Status.ON);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        assertEquals(Status.ON, greenhouse.getStatus());
    }

    @Test
    void updateTelemetry_setsLastUpdate() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));
        assertNull(greenhouse.getLastUpdate());

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        assertNotNull(greenhouse.getLastUpdate());
    }

    @Test
    void updateTelemetry_savesGreenhouse() {
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        verify(greenhouseRepository).save(greenhouse);
    }

    // -------------------------------------------------------------------------
    // Model push on reconnect — critical for delivering requestedValue changes
    // that happened while the device was offline
    // -------------------------------------------------------------------------

    @Test
    void updateTelemetry_pushesModelToDevice_whenDeviceWasNotResponsive() throws Exception {
        greenhouse.setStatus(Status.NOT_RESPONSIVE);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1}");

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(mqttPublisher).sendCommand(topicCaptor.capture(), any());
        assertEquals("greenhouse/192.168.1.100/set/model", topicCaptor.getValue(),
                "Model must be pushed to the device that just reconnected");
    }

    @Test
    void updateTelemetry_pushesModelToDevice_whenDeviceWasOff() throws Exception {
        greenhouse.setStatus(Status.OFF);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1}");

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        verify(mqttPublisher).sendCommand(contains("192.168.1.100"), any());
    }

    @Test
    void updateTelemetry_doesNotPushModel_whenDeviceWasAlreadyOn() throws Exception {
        greenhouse.setStatus(Status.ON);
        when(greenhouseRepository.findById(1L)).thenReturn(Optional.of(greenhouse));

        greenhouseService.updateTelemetry(buildTelemetry(5L, 22.0, null));

        verify(mqttPublisher, never()).sendCommand(any(), any());
    }

    @Test
    void updateTelemetry_doesNothingForUnknownGreenhouseId() {
        when(greenhouseRepository.findById(99L)).thenReturn(Optional.empty());

        TelemetryGreenhouseDTO telemetry = new TelemetryGreenhouseDTO();
        telemetry.id = 99L;
        telemetry.zones = new ArrayList<>();

        assertDoesNotThrow(() -> greenhouseService.updateTelemetry(telemetry));
        verify(greenhouseRepository, never()).save(any());
        verifyNoInteractions(mqttPublisher);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds the minimal telemetry DTO that the Arduino sends:
     *   { "id": 1, "zones": [{ "id": 1, "parameters": [{ "id": paramId, "val": val }] }] }
     *
     * Pass null for val to simulate an Arduino omitting the field (ArduinoJson default).
     * Pass null for fpDto to skip flowerpots.
     */
    private TelemetryGreenhouseDTO buildTelemetry(Long paramId, Double val, TelemetryFlowerpotDTO fpDto) {
        TelemetryParameterDTO pDto = new TelemetryParameterDTO();
        pDto.id = paramId;
        pDto.val = val;

        TelemetryZoneDTO zDto = new TelemetryZoneDTO();
        zDto.id = 1L;
        zDto.parameters = List.of(pDto);
        zDto.flowerpots = fpDto != null ? List.of(fpDto) : new ArrayList<>();

        TelemetryGreenhouseDTO telemetry = new TelemetryGreenhouseDTO();
        telemetry.id = 1L;
        telemetry.zones = List.of(zDto);
        return telemetry;
    }
}