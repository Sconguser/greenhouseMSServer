package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Flowerpot;
import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PlantMapper.class})
public interface FlowerpotMapper {
    FlowerpotMapper INSTANCE = Mappers.getMapper(FlowerpotMapper.class);

    Flowerpot toEntity (FlowerpotRequest request);

    FlowerpotResponse toResponse (Flowerpot flowerpot);

}

