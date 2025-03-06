package com.example;

import com.influxdb.query.FluxRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record TemperatureResponse(
        String location,
        Float value,
        LocalDateTime time
) {

    public static TemperatureResponse from(FluxRecord fluxRecord) {
        String location = (String) fluxRecord.getValueByKey("location");
        float value = ((Number) fluxRecord.getValue()).floatValue();
        Instant instant = fluxRecord.getTime();
        LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return new TemperatureResponse(location, value, time);
    }

}
