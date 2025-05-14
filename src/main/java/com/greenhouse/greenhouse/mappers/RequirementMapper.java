package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.dtos.RequirementDTO;
import com.greenhouse.greenhouse.dtos.ToggleRequirementDTO;
import com.greenhouse.greenhouse.dtos.ValueRequirementDTO;
import com.greenhouse.greenhouse.models.RequirementEntity;
import com.greenhouse.greenhouse.models.ToggleRequirementEntity;
import com.greenhouse.greenhouse.models.ValueRequirementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface RequirementMapper {
    @SubclassMapping(source = ValueRequirementDTO.class, target = ValueRequirementEntity.class)
    @SubclassMapping(source = ToggleRequirementDTO.class, target = ToggleRequirementEntity.class)
    RequirementEntity toEntity (RequirementDTO requirementDTO);


    @SubclassMapping(source = ValueRequirementEntity.class, target = ValueRequirementDTO.class)
    @SubclassMapping(source = ToggleRequirementEntity.class, target = ToggleRequirementDTO.class)
    RequirementDTO toDto (RequirementEntity entity);
}
