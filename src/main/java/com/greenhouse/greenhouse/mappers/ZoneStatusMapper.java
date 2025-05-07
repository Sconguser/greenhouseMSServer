package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.ZoneStatus;
import com.greenhouse.greenhouse.requests.ZoneStatusRequest;
import com.greenhouse.greenhouse.responses.ZoneStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ZoneStatusMapper {
    ZoneStatusMapper INSTANCE = Mappers.getMapper(ZoneStatusMapper.class);

    ZoneStatus toEntity(ZoneStatusRequest request, Greenhouse greenhouse);

    ZoneStatusResponse toResponse (ZoneStatus zoneStatus);

}
