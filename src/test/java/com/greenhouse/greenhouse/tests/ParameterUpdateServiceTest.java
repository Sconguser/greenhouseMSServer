package com.greenhouse.greenhouse.tests;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.FlowerpotRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ParameterRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
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

/** Unit tests for ParameterService.updateParameters(). */
@ExtendWith(MockitoExtension.class)
class ParameterUpdateServiceTest {

    @Mock ParameterMapper parameterMapper;
    @Mock ParameterRepository parameterRepository;
    @Mock GreenhouseRepository greenhouseRepository;
    @Mock ZoneRepository zoneRepository;
    @Mock FlowerpotRepository flowerpotRepository;

    @InjectMocks ParameterService parameterService;

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
        verifyNoInteractions(parameterRepository);
    }

    @Test
    void updateParameters_returnsEmpty_forNullInput() {
        List<ParameterDTO> result = parameterService.updateParameters(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(parameterRepository);
    }

    @Test
    void updateParameters_skipsDtoWithNullId() {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(null);
        dto.setRequestedValue(25.0);

        parameterService.updateParameters(List.of(dto));

        verifyNoInteractions(parameterRepository);
    }

    @Test
    void updateParameters_skipsDtoWithNullRequestedValue() {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(10L);
        dto.setRequestedValue(null);

        parameterService.updateParameters(List.of(dto));

        verifyNoInteractions(parameterRepository);
    }

    @Test
    void updateParameters_skipsUnknownParameterId() {
        when(parameterRepository.findById(999L)).thenReturn(Optional.empty());

        parameterService.updateParameters(List.of(dto(999L, 25.0)));

        verify(parameterRepository, never()).save(any());
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

    private ParameterEntity zoneParam(Long id, Zone zone) {
        ParameterEntity p = new ParameterEntity();
        p.setId(id);
        p.setZone(zone);
        return p;
    }

}