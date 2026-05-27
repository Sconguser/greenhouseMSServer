package com.greenhouse.greenhouse.models;

public enum GreenhouseEventType {
    /** Device reconnected after being offline / not responsive. */
    BOOT,
    /** Device stopped responding while it was ON (unexpected offline / crash). */
    CRASH
}