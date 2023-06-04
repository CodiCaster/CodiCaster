package com.ll.codicaster.boundedContext.weather.controller;

import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;


}
