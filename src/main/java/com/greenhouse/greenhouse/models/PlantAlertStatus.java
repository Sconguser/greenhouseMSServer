package com.greenhouse.greenhouse.models;

public enum PlantAlertStatus {
    /** Out of range, but not yet sustained long enough to alert the user. */
    PENDING,
    /** Sustained breach — the user has been (or will be) notified. */
    ACTIVE,
    /** Conditions returned to the acceptable range. */
    RESOLVED
}
