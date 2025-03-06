package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/temperature")
@RestController
public class TemperatureController {

    private final TemperatureService temperatureService;

    @PostMapping
    public String addTemperature(@RequestBody TemperatureRequest request) {
        temperatureService.addTemperature(request);
        return "저장 성공!";
    }

    @GetMapping
    public List<TemperatureResponse> getTemperatures(
            @RequestParam String location,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime stop
    ) {
        return temperatureService.getTemperatures(location, start, stop);
    }

}
