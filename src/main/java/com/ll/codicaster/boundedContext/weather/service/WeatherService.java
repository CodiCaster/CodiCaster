package com.ll.codicaster.boundedContext.weather.service;

import com.ll.codicaster.base.event.EventAfterSaveWeather;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final WeatherAPIService weatherAPIService;
    private final ApplicationEventPublisher publisher;

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
        String weatherInfo = " " + weather.getTmp() + "°C";

        if (weather.getPty() == 1 || weather.getPty() == 4) {
            return "\uD83C\uDF27️" + weatherInfo;
        }
        if (weather.getPty() == 2 || weather.getPty() == 3) {
            return "️️\uD83C\uDF28️" + weatherInfo;
        }
        if (weather.getSky() == 1) {
            return "\uD83C\uDF24" + weatherInfo;
        }
        if (weather.getSky() == 3) {
            return "\uD83C\uDF1E" + weatherInfo;
        }
        return "☁" + weatherInfo;
    }

    public void whenAfterWrite(Rq rq, Long articleId) {
        Location location = rq.getCurrentLocation();
        Weather weather = getWeather(location);
        weather.setArticleId(articleId);
        Weather savedWeather = weatherRepository.save(weather);
        publisher.publishEvent(new EventAfterSaveWeather(this, savedWeather.getId(), articleId));
    }
}
