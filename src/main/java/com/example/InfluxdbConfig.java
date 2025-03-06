package com.example;

import com.influxdb.LogLevel;
import com.influxdb.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxdbConfig {

    /**
     * 비동기 동작 (응답 대기 x)
     * 데이터를 모아서 한번에 write
     */
    @Bean
    public WriteApi writeApi(InfluxDBClient influxDBClient) {
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(1000) // 한 번에 처리할 데이터 개수
                .flushInterval(1000) // 설정 시간(초)마다 저장
                .bufferLimit(10000) // 버퍼 사이즈 조정
                .build();
        return influxDBClient.makeWriteApi(writeOptions);
    }

    /**
     * 동기식 (응답 대기)
     */
    @Bean
    public WriteApiBlocking writeApiBlocking(InfluxDBClient influxDBClient) {
        return influxDBClient.getWriteApiBlocking();
    }

    /**
     * 쿼리 API (조회용)
     */
    @Bean
    public QueryApi queryApi(InfluxDBClient influxDBClient) {
        return influxDBClient.getQueryApi();
    }

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxDBClientOptions());
    }

    @Bean
    public InfluxDBClientOptions influxDBClientOptions() {
        return InfluxDBClientOptions.builder()
                .url("http://127.0.0.1:8086")
                .authenticateToken("api token 값을 입력해주세요".toCharArray())
                .org("org1")
                .bucket("sensor")
                .logLevel(LogLevel.BASIC) // NONE, BASIC, HEADERS, BODY
                .build();
    }

}
