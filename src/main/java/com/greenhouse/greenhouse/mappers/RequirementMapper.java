package com.greenhouse.greenhouse.mappers;

import com.greenhouse.greenhouse.dtos.RequirementDTO;
import com.greenhouse.greenhouse.models.Requirement;
import com.greenhouse.greenhouse.models.RequirementEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequirementMapper {
    RequirementEntity toEntity (RequirementDTO requirementDTO);


    RequirementDTO toDto (RequirementEntity entity);

    RequirementDTO toDto (Requirement requirement);

    Requirement toObject (RequirementDTO requirementDTO);

}

