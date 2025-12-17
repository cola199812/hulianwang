package com.outdoor.demo.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RouteCreateRequest {
    @NotBlank
    private String name;
    @NotNull
    private Double distance;
    @NotBlank
    private String level;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

