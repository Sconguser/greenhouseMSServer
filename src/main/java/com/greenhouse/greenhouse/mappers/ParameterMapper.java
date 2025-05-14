package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.dtos.ToggleParameterDTO;
import com.greenhouse.greenhouse.dtos.ValueParameterDTO;
import com.greenhouse.greenhouse.models.ParameterEntity;
import com.greenhouse.greenhouse.models.ToggleParameterEntity;
import com.greenhouse.greenhouse.models.ValueParameterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface ParameterMapper {
    @SubclassMapping(source = ValueParameterDTO.class, target = ValueParameterEntity.class)
    @SubclassMapping(source = ToggleParameterDTO.class, target = ToggleParameterEntity.class)
    ParameterEntity toEntity (ParameterDTO dto);

    @SubclassMapping(source = ValueParameterEntity.class, target = ValueParameterDTO.class)
    @SubclassMapping(source = ToggleParameterEntity.class, target = ToggleParameterDTO.class)
    ParameterDTO toDto (ParameterEntity entity);
}
