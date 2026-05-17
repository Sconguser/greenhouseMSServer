package com.greenhouse.greenhouse.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that the JSON the server serializes and sends to the Arduino over MQTT
 * matches the field names and value formats that the Arduino's parseGreenhouseJson()
 * and deserializeParameter() functions expect.
 *
 * Key Arduino expectations (from model.cpp / deserializeParameter):
 *   src["id"], src["name"], src["mutable"] (NOT "mutableFlag"),
 *   src["currentValue"], src["requestedValue"], src["min"], src["max"],
 *   src["unit"], src["parameterType"]  (checked via equalsIgnoreCase("TOGGLE"))
 */
class ModelSerializationTest {

    private ObjectMapper mapper;
    private Greenhouse greenhouse;
    private ParameterEntity zoneParam;
    private ParameterEntity flowerpotParam;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

        // --- build test hierarchy ---
        greenhouse = new Greenhouse();
        greenhouse.setId(1L);
        greenhouse.setName("Test Greenhouse");
        greenhouse.setLocation("Lab");
        greenhouse.setIpAddress("192.168.1.100");
        greenhouse.setStatus(Status.ON);

        zoneParam = new ParameterEntity();
        zoneParam.setId(5L);
        zoneParam.setName("Temperature");
        zoneParam.setMutable(true);
        zoneParam.setCurrentValue(22.5);
        zoneParam.setRequestedValue(25.0);
        zoneParam.setMin(15.0);
        zoneParam.setMax(35.0);
        zoneParam.setUnit("C");
        zoneParam.setParameterType(ParameterType.TOGGLE);

        flowerpotParam = new ParameterEntity();
        flowerpotParam.setId(10L);
        flowerpotParam.setName("SoilMoisture");
        flowerpotParam.setMutable(false);
        flowerpotParam.setCurrentValue(45.0);
        flowerpotParam.setRequestedValue(null);
        flowerpotParam.setMin(20.0);
        flowerpotParam.setMax(80.0);
        flowerpotParam.setUnit("%");
        flowerpotParam.setParameterType(ParameterType.VALUE);

        Flowerpot flowerpot = new Flowerpot();
        flowerpot.setId(1L);
        flowerpot.setName("Pot 1");
        flowerpot.setParameters(List.of(flowerpotParam));
        flowerpotParam.setFlowerpot(flowerpot);

        Zone zone = new Zone();
        zone.setId(1L);
        zone.setName("Zone 1");
        zone.setParameters(List.of(zoneParam));
        zone.setFlowerpots(List.of(flowerpot));
        zone.setGreenhouse(greenhouse);
        zoneParam.setZone(zone);
        flowerpot.setZone(zone);

        greenhouse.addZone(zone);
    }

    // -------------------------------------------------------------------------
    // Field name checks — Arduino reads "mutable", NOT "mutableFlag"
    // -------------------------------------------------------------------------

    @Test
    void zoneParameter_serializesWithMutableKey_notMutableFlag() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode paramNode = firstZoneParam(json);

        // @JsonProperty("mutable") on isMutable() pins the key to what the Arduino reads.
        // "mutableFlag" would be silently ignored by ArduinoJson and the actuator would
        // never react to requestedValue changes.
        assertTrue(paramNode.has("mutable"));
        assertFalse(paramNode.has("mutableFlag"));
        assertTrue(paramNode.get("mutable").asBoolean());
    }

    @Test
    void flowerpotParameter_serializesWithMutableKey_notMutableFlag() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode paramNode = firstFlowerpotParam(json);

        assertTrue(paramNode.has("mutable"));
        assertFalse(paramNode.has("mutableFlag"));
        assertFalse(paramNode.get("mutable").asBoolean());
    }

    // -------------------------------------------------------------------------
    // parameterType value — Arduino does equalsIgnoreCase("TOGGLE")
    // @JsonProperty("Toggle") on the enum produces "Toggle"; equalsIgnoreCase matches
    // -------------------------------------------------------------------------

    @Test
    void toggleParameter_serializesAsToggle_notUppercaseTOGGLE() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode paramNode = firstZoneParam(json);

        assertEquals("Toggle", paramNode.get("parameterType").asText(),
                "@JsonProperty(\"Toggle\") must serialize to \"Toggle\"; Arduino's equalsIgnoreCase(\"TOGGLE\") will match it");
    }

    @Test
    void valueParameter_serializesAsValue() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode paramNode = firstFlowerpotParam(json);

        assertEquals("Value", paramNode.get("parameterType").asText());
    }

    // -------------------------------------------------------------------------
    // All fields the Arduino reads must be present
    // -------------------------------------------------------------------------

    @Test
    void zoneParameter_hasAllArduinoRequiredFields() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode param = firstZoneParam(json);

        assertAll(
            () -> assertTrue(param.has("id"),             "id required"),
            () -> assertTrue(param.has("name"),           "name required"),
            () -> assertTrue(param.has("mutable"),        "mutable required"),
            () -> assertTrue(param.has("currentValue"),   "currentValue required"),
            () -> assertTrue(param.has("requestedValue"), "requestedValue required"),
            () -> assertTrue(param.has("min"),            "min required"),
            () -> assertTrue(param.has("max"),            "max required"),
            () -> assertTrue(param.has("unit"),           "unit required"),
            () -> assertTrue(param.has("parameterType"),  "parameterType required")
        );
    }

    // -------------------------------------------------------------------------
    // Value correctness
    // -------------------------------------------------------------------------

    @Test
    void zoneParameter_valuesMatchEntity() throws Exception {
        String json = mapper.writeValueAsString(greenhouse);
        JsonNode param = firstZoneParam(json);

        assertEquals(5,    param.get("id").asLong());
        assertEquals("Temperature", param.get("name").asText());
        assertTrue(param.get("mutable").asBoolean());
        assertEquals(22.5, param.get("currentValue").asDouble(), 0.001);
        assertEquals(25.0, param.get("requestedValue").asDouble(), 0.001);
        assertEquals(15.0, param.get("min").asDouble(), 0.001);
        assertEquals(35.0, param.get("max").asDouble(), 0.001);
        assertEquals("C",  param.get("unit").asText());
    }

    // -------------------------------------------------------------------------
    // Top-level greenhouse structure the Arduino's parseGreenhouseJson reads
    // -------------------------------------------------------------------------

    @Test
    void greenhouse_hasTopLevelFields() throws Exception {
        JsonNode root = mapper.readTree(mapper.writeValueAsString(greenhouse));

        assertAll(
            () -> assertEquals(1L, root.get("id").asLong()),
            () -> assertEquals("192.168.1.100", root.get("ipAddress").asText(),
                    "ipAddress is used by publishTelemetryJson — must survive the model push"),
            () -> assertTrue(root.has("zones"), "zones array required"),
            () -> assertTrue(root.get("zones").isArray())
        );
    }

    @Test
    void greenhouse_zoneHasIdAndParameters() throws Exception {
        JsonNode root = mapper.readTree(mapper.writeValueAsString(greenhouse));
        JsonNode zone = root.get("zones").get(0);

        assertEquals(1L, zone.get("id").asLong());
        assertTrue(zone.has("parameters"));
        assertTrue(zone.get("parameters").isArray());
    }

    @Test
    void greenhouse_flowerpotHasIdAndParameters() throws Exception {
        JsonNode root = mapper.readTree(mapper.writeValueAsString(greenhouse));
        JsonNode flowerpot = root.get("zones").get(0).get("flowerpots").get(0);

        assertEquals(1L, flowerpot.get("id").asLong());
        assertTrue(flowerpot.has("parameters"));
    }

    // -------------------------------------------------------------------------
    // No circular reference (Plant ↔ Flowerpot) — must not throw StackOverflowError
    // -------------------------------------------------------------------------

    @Test
    void greenhouse_withPlantInFlowerpot_doesNotCauseInfiniteLoop() throws Exception {
        Plant plant = new Plant();
        plant.setId(99L);
        plant.setName("Basil");

        Flowerpot fp = greenhouse.getZones().get(0).getFlowerpots().get(0);
        fp.setPlants(List.of(plant));

        assertDoesNotThrow(() -> mapper.writeValueAsString(greenhouse),
                "Plant.flowerPots and Plant.requirements must be @JsonIgnore to prevent StackOverflowError");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private JsonNode firstZoneParam(String json) throws Exception {
        return mapper.readTree(json)
                     .get("zones").get(0)
                     .get("parameters").get(0);
    }

    private JsonNode firstFlowerpotParam(String json) throws Exception {
        return mapper.readTree(json)
                     .get("zones").get(0)
                     .get("flowerpots").get(0)
                     .get("parameters").get(0);
    }
}