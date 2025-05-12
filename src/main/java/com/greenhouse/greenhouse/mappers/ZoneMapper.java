package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.models.Zone;
import com.greenhouse.greenhouse.requests.ZoneRequest;
import com.greenhouse.greenhouse.responses.ZoneResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FlowerpotMapper.class})
public interface ZoneMapper {
    ZoneMapper INSTANCE = Mappers.getMapper(ZoneMapper.class);

    Zone toEntity (ZoneRequest request);

    ZoneResponse toResponse (Zone zone);
}

