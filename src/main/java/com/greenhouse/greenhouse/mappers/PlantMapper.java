package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.PlantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlantMapper {
    PlantMapper INSTANCE = Mappers.getMapper(PlantMapper.class);

    Plant toEntity (PlantRequest request);

    PlantResponse toResponse (Plant plant);
}
