package com.greenhouse.greenhouse.components;

import com.greenhouse.greenhouse.models.Parameter;
import com.greenhouse.greenhouse.models.Requirement;
import org.springframework.stereotype.Component;

@Component
public class RequirementChecker {
    boolean requirementMet (Requirement requirement, Parameter parameter) {
        return parameter.getCurrentValue() >= requirement.getLowerThreshold() && parameter.getCurrentValue() <= requirement.getUpperThreshold();
    }
}
