package com.greenhouse.greenhouse.responses;

import java.util.ArrayList;
import java.util.List;

public class ZoneResponse {
    private Long id;
    private String name;
    private List<FlowerpotResponse> flowerpots = new ArrayList();

    public ZoneResponse (Long id, String name, List<FlowerpotResponse> flowerpots) {
        this.id = id;
        this.name = name;
        if (flowerpots != null) {
            this.flowerpots.addAll(flowerpots);
        }
    }
}
