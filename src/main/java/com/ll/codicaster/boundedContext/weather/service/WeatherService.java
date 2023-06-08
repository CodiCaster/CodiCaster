package com.ll.codicaster.boundedContext.weather.service;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final WeatherAPIService weatherAPIService;

    @Transactional
    public Long save(Location location) {
        Weather weather = getWeather(location);
        Weather savedWeather = weatherRepository.save(weather);
        return savedWeather.getId();
    }

    public Weather getWeather(Location location) {
        return weatherAPIService.getApiWeather(location.getPointX(), location.getPointY());
    }

    public Weather getWeather(Long id) {
        return weatherRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No Weather Found with id: " + id));
    }

    @Transactional
    public RsData deleteById(Long weatherId) {
        weatherRepository.deleteById(weatherId);
        return RsData.of("S-1", "날씨 정보를 삭제하였습니다.");
    }

    public String getWeatherInfo(Location location) {
        Weather weather = getWeather(location);
        return getWeatherInfo(weather);
    }

    public String getWeatherInfo(Weather weather) {
        String weatherInfo = "";

        if (weather.getSky() < 5) {
            weatherInfo += "️️\uD83C\uDF24 ";
        } else {
            weatherInfo += "☁️ ";
        }
        weatherInfo += weather.getTmp() + "°C";
        return weatherInfo;
    }
}
