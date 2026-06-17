package com.greenhouse.greenhouse.models;

/**
 * Discriminates how a plant {@link RequirementEntity} is evaluated against the
 * live conditions. Currently only THRESHOLD is implemented; the discriminator
 * exists so additional kinds (e.g. WATERING/time-based) can be added without a
 * schema migration of existing requirements.
 */
public enum RequirementKind {
    /** currentValue must stay within [lowerThreshold, upperThreshold]. */
    THRESHOLD
}
