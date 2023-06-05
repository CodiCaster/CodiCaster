package com.ll.codicaster.boundedContext.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.entity.Point;
import com.ll.codicaster.boundedContext.location.repository.LocationRepository;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

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

}
