package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.GreenhouseStatus;
import com.greenhouse.greenhouse.requests.GreenhouseStatusRequest;
import com.greenhouse.greenhouse.responses.GreenhouseStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GreenhouseStatusMapper {
    GreenhouseStatusMapper INSTANCE = Mappers.getMapper(GreenhouseStatusMapper.class);

    @Mapping(source = "request.status", target = "status")
    GreenhouseStatus toEntity(GreenhouseStatusRequest request, Greenhouse greenhouse);

    GreenhouseStatusResponse toResponse (GreenhouseStatus greenhouseStatus);

}
