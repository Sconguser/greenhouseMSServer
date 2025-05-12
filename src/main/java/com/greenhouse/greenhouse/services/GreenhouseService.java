package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.GreenhouseNotFoundException;
import com.greenhouse.greenhouse.mappers.GreenhouseMapper;
import com.greenhouse.greenhouse.mappers.ZoneMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.Zone;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.PlantRepository;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.ZoneRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import com.greenhouse.greenhouse.responses.ZoneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseMapper greenhouseMapper;
    private final ZoneMapper zoneMapper;

    @Autowired
    public GreenhouseService (GreenhouseRepository greenhouseRepository, PlantRepository plantRepository,
                              GreenhouseMapper greenhouseMapper, ZoneMapper zoneMapper)
    {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.zoneMapper = zoneMapper;
    }

    public GreenhouseResponse getGreenhouse (Long id) {
        return greenhouseMapper.toResponse(getGreenhouseEntity(id));
    }

    public List<GreenhouseResponse> getAllGreenhouses () {
        return greenhouseRepository.findAll()
                .stream()
                .map(greenhouseMapper::toResponse)
                .toList();
    }

    public Greenhouse getGreenhouseEntity (Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new GreenhouseNotFoundException(
                        "Requested greenhouse with ID = " + id + "was not found"));
    }

    public GreenhouseResponse addGreenhouse(GreenhouseRequest request){
        Greenhouse greenhouse = greenhouseMapper.toEntity(request);
        Greenhouse greenhouseRepository = this.greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toResponse(greenhouseRepository);
    }

    public GreenhouseResponse updateGreenhouse (GreenhouseRequest request, Long id) {
        Greenhouse greenhouse = getGreenhouseEntity(id);
        if (request.getName() != null) {
            greenhouse.setName(request.getName());
        }
        if (request.getIpAddress() != null) {
            greenhouse.setIpAddress(request.getIpAddress());
        }
        if (request.getLocation() != null) {
            greenhouse.setLocation(request.getLocation());
        }
        greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toResponse(greenhouse);
    }

    public void deleteGreenhouse (Long id) {
        greenhouseRepository.deleteById(id);
    }

    public ZoneResponse addZone (Long id, ZoneRequest zoneRequest) {
        Greenhouse greenhouse = getGreenhouseEntity(id);
        Zone zone = zoneMapper.toEntity(zoneRequest);
        greenhouse.addZone(zone);
        greenhouseRepository.save(greenhouse);
        return zoneMapper.toResponse(zone);
    }
}
