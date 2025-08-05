package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.models.Parameter;
import com.greenhouse.greenhouse.models.ParameterEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParameterMapper {
    ParameterEntity toEntity (ParameterDTO dto);

    ParameterDTO toDto (ParameterEntity entity);

    ParameterDTO toDto(Parameter parameter);

    Parameter toObject(ParameterDTO dto);

}
