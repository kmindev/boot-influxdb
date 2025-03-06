package com.example;

import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TemperatureService {

    private final WriteApi writeApi; // 비동기식: 데이터를 모아서 한번에 write
    private final WriteApiBlocking writeApiBlocking; // 동기식: 즉시 요청 - 응답
    private final QueryApi queryApi; // 쿼리 API(조회용)

    public void addTemperature(TemperatureRequest request) {
        Temperature temperature = Temperature.of(request.location(), request.value());
        writeApi.writeMeasurement(WritePrecision.S, temperature); // 정밀도 설정(S: 초)
    }

    public void addTemperatureBlocking(TemperatureRequest request) {
        Temperature temperature = Temperature.of(request.location(), request.value());
        writeApiBlocking.writeMeasurement(WritePrecision.S, temperature); // 정밀도 설정(S: 초)
    }


    public List<TemperatureResponse> getTemperatures(String location, LocalDateTime start, LocalDateTime stop) {
        String query = createQuery(location, start, stop);
        List<FluxTable> tables = queryApi.query(query);
        return tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .map(TemperatureResponse::from)
                .toList();
    }

    /*
    import "timezone"
    option location = timezone.fixed(offset: 9h)

    from(bucket:"sensor")
	|> range(start:2025-03-05T01:00:00.000000000Z, stop:2025-03-07T01:00:00.000000000Z)
	|> filter(fn: (r) => (r["_measurement"] == "temperature" and r["_field"] == "value" and r["location"] == "거실"))
	|> aggregateWindow(every:1s, fn:mean, column:"_value", timeSrc:"_start", timeDst:"_time", createEmpty:false)
     */
    private String createQuery(String location, LocalDateTime start, LocalDateTime stop) {
        // LocalDateTime을 Instan 타입으로 변환
        Instant startInstant = start.atZone(ZoneId.systemDefault()).toInstant();
        Instant stopInstant = stop.atZone(ZoneId.systemDefault()).toInstant();

        return Flux.from("sensor")
                .withLocationFixed("9h") // 타임존 설정(서울=UTC+9)
                .range(startInstant, stopInstant) // 시간 범위 지정
                .filter(Restrictions.and(
                        Restrictions.measurement().equal("temperature"),
                        Restrictions.field().equal("value"),
                        Restrictions.tag("location").equal(location))
                )
                .aggregateWindow()
                .withEvery("1s")
                .withAggregateFunction("mean") // 평균값
                .withColumn("_value")
                .withTimeSrc("_start")
                .withTimeDst("_time")
                .withCreateEmpty(false)
                .toString();
    }

}
