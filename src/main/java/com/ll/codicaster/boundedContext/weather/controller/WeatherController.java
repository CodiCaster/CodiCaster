package com.ll.codicaster.boundedContext.weather.controller;

import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usr/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
}
