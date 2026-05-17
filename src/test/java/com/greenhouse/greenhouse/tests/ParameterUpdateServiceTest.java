package com.greenhouse.greenhouse.tests;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.FlowerpotRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ParameterRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
import com.greenhouse.greenhouse.services.GreenhouseService;
import com.greenhouse.greenhouse.services.ParameterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ParameterService.updateParameters().
 *
 * Critical invariant: after saving the new requestedValue to the DB,
 * the service must push the FULL greenhouse model to the device — not a
 * partial model. The Arduino's parseGreenhouseJson() does an atomic swap, so
 * any partial model would corrupt the device's in-memory state (losing
 * ipAddress, zones not included in the partial JSON, etc.).
 */
@ExtendWith(MockitoExtension.class)
class ParameterUpdateServiceTest {

    @Mock ParameterMapper parameterMapper;
    @Mock ParameterRepository parameterRepository;
    @Mock GreenhouseRepository greenhouseRepository;
    @Mock ZoneRepository zoneRepository;
    @Mock FlowerpotRepository flowerpotRepository;
    @Mock GreenhouseService greenhouseService;

    @InjectMocks ParameterService parameterService;

    // -------------------------------------------------------------------------
    // Full model push — the most critical invariant
    // -------------------------------------------------------------------------

    @Test
    void updateParameters_pushesFullModelViaGreenhouseService_forZoneParam() {
        ParameterEntity param = zoneParam(10L, zoneInGreenhouse(1L, 42L));
        when(parameterRepository.findById(10L)).thenReturn(Optional.of(param));

        parameterService.updateParameters(List.of(dto(10L, 25.0)));

        verify(greenhouseService).sendGreenhouseDataToGreenhouse(42L);
    }

    @Test
    void updateParameters_pushesFullModelViaGreenhouseService_forFlowerpotParam() {
        ParameterEntity param = flowerpotParam(20L, flowerpotInGreenhouse(2L, 99L));
        when(parameterRepository.findById(20L)).thenReturn(Optional.of(param));

        parameterService.updateParameters(List.of(dto(20L, 60.0)));

        verify(greenhouseService).sendGreenhouseDataToGreenhouse(99L);
    }

    @Test
    void updateParameters_pushesFullModelViaGreenhouseService_forGreenhouseParam() {
        Greenhouse gh = greenhouse(7L);
        ParameterEntity param = new ParameterEntity();
        param.setId(30L);
        param.setGreenhouse(gh);

        when(parameterRepository.findById(30L)).thenReturn(Optional.of(param));

        parameterService.updateParameters(List.of(dto(30L, 18.0)));

        verify(greenhouseService).sendGreenhouseDataToGreenhouse(7L);
    }

    @Test
    void updateParameters_pushesModelOnce_evenWithMultipleParamsInSameGreenhouse() {
        Zone zone = zoneInGreenhouse(1L, 5L);
        ParameterEntity p1 = zoneParam(10L, zone);
        ParameterEntity p2 = zoneParam(11L, zone);

        when(parameterRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(parameterRepository.findById(11L)).thenReturn(Optional.of(p2));

        parameterService.updateParameters(List.of(dto(10L, 25.0), dto(11L, 30.0)));

        // Only one push regardless of how many parameters were updated
        verify(greenhouseService, times(1)).sendGreenhouseDataToGreenhouse(5L);
    }

    // -------------------------------------------------------------------------
    // DB update — requestedValue must be persisted
    // -------------------------------------------------------------------------

    @Test
    void updateParameters_savesNewRequestedValue() {
        ParameterEntity param = zoneParam(10L, zoneInGreenhouse(1L, 1L));
        param.setRequestedValue(10.0);

        when(parameterRepository.findById(10L)).thenReturn(Optional.of(param));

        parameterService.updateParameters(List.of(dto(10L, 25.0)));

        assertEquals(25.0, param.getRequestedValue(), 0.001);
        verify(parameterRepository).save(param);
    }

    @Test
    void updateParameters_savesEachParameterIndividually() {
        Zone zone = zoneInGreenhouse(1L, 1L);
        ParameterEntity p1 = zoneParam(10L, zone);
        ParameterEntity p2 = zoneParam(11L, zone);

        when(parameterRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(parameterRepository.findById(11L)).thenReturn(Optional.of(p2));

        parameterService.updateParameters(List.of(dto(10L, 25.0), dto(11L, 30.0)));

        verify(parameterRepository).save(p1);
        verify(parameterRepository).save(p2);
    }

    // -------------------------------------------------------------------------
    // Guard clauses — skip invalid DTOs, don't push if nothing changed
    // -------------------------------------------------------------------------

    @Test
    void updateParameters_returnsEmpty_forEmptyInput() {
        List<ParameterDTO> result = parameterService.updateParameters(Collections.emptyList());

        assertTrue(result.isEmpty());
        verifyNoInteractions(parameterRepository, greenhouseService);
    }

    @Test
    void updateParameters_returnsEmpty_forNullInput() {
        List<ParameterDTO> result = parameterService.updateParameters(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(parameterRepository, greenhouseService);
    }

    @Test
    void updateParameters_skipsDtoWithNullId() {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(null);
        dto.setRequestedValue(25.0);

        parameterService.updateParameters(List.of(dto));

        verifyNoInteractions(parameterRepository, greenhouseService);
    }

    @Test
    void updateParameters_skipsDtoWithNullRequestedValue() {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(10L);
        dto.setRequestedValue(null); // guard clause filters this out before any repo call

        parameterService.updateParameters(List.of(dto));

        verifyNoInteractions(parameterRepository, greenhouseService);
    }

    @Test
    void updateParameters_skipsUnknownParameterId() {
        when(parameterRepository.findById(999L)).thenReturn(Optional.empty());

        parameterService.updateParameters(List.of(dto(999L, 25.0)));

        verify(parameterRepository, never()).save(any());
        verify(greenhouseService, never()).sendGreenhouseDataToGreenhouse(any());
    }

    // -------------------------------------------------------------------------
    // Regression: the old code built a PARTIAL model that corrupted the Arduino.
    // Verify the new code does NOT call any raw MQTT method directly.
    // -------------------------------------------------------------------------

    @Test
    void updateParameters_neverCallsMqttDirectly() {
        // The service must NOT have a direct MqttPublisher dependency anymore.
        // All MQTT goes through GreenhouseService.sendGreenhouseDataToGreenhouse().
        // This test ensures the delegation pattern stays in place.
        ParameterEntity param = zoneParam(10L, zoneInGreenhouse(1L, 1L));
        when(parameterRepository.findById(10L)).thenReturn(Optional.of(param));

        parameterService.updateParameters(List.of(dto(10L, 25.0)));

        // If ParameterService had re-introduced a direct MqttPublisher field,
        // it would not be in the @InjectMocks list and would be null → NPE here.
        // Passing without NPE confirms the delegation is intact.
        verify(greenhouseService).sendGreenhouseDataToGreenhouse(any());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private ParameterDTO dto(Long id, double requestedValue) {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(id);
        dto.setRequestedValue(requestedValue);
        return dto;
    }

    private Greenhouse greenhouse(Long id) {
        Greenhouse gh = new Greenhouse();
        gh.setId(id);
        gh.setIpAddress("192.168.1." + id);
        return gh;
    }

    private Zone zoneInGreenhouse(Long zoneId, Long greenhouseId) {
        Zone zone = new Zone();
        zone.setId(zoneId);
        zone.setGreenhouse(greenhouse(greenhouseId));
        return zone;
    }

    private Flowerpot flowerpotInGreenhouse(Long flowerpotId, Long greenhouseId) {
        Flowerpot fp = new Flowerpot();
        fp.setId(flowerpotId);
        fp.setZone(zoneInGreenhouse(1L, greenhouseId));
        return fp;
    }

    private ParameterEntity zoneParam(Long id, Zone zone) {
        ParameterEntity p = new ParameterEntity();
        p.setId(id);
        p.setZone(zone);
        return p;
    }

    private ParameterEntity flowerpotParam(Long id, Flowerpot fp) {
        ParameterEntity p = new ParameterEntity();
        p.setId(id);
        p.setFlowerpot(fp);
        return p;
    }
}