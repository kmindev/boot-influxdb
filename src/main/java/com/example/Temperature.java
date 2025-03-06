package com.example;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Measurement(name = "temperature")
public class Temperature {
    @Column(tag = true) private String location;
    @Column private float value;
    @Column(timestamp = true) private Instant time;

    public static Temperature of(String location, float value) {
        return new Temperature(location, value, Instant.now());
    }

}
