package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {GreenhouseStatusMapper.class, PlantMapper.class})
public interface GreenhouseMapper {
    GreenhouseMapper INSTANCE = Mappers.getMapper(GreenhouseMapper.class);

    Greenhouse toEntity (GreenhouseRequest request);

    GreenhouseResponse toResponse (Greenhouse greenhouse);


}
