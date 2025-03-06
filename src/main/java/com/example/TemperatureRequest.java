package com.example;

public record TemperatureRequest(
        String location,
        float value
) {
}
