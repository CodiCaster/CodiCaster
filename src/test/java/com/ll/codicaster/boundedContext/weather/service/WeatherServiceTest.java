package com.ll.codicaster.boundedContext.weather.service;

import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.entity.LocationConstants;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.repository.WeatherRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private WeatherRepository weatherRepository;

    private Location initialLocation;
    private Weather initialWeather;

    @BeforeEach
    void init() {
        initialLocation = Location.builder()
                .id(1L)
                .latitude(LocationConstants.LATITUDE)
                .longitude(LocationConstants.LONGITUDE)
                .pointX(LocationConstants.POINT_X)
                .pointY(LocationConstants.POINT_Y)
                .address(LocationConstants.ADDRESS)
                .build();
        initialWeather = Weather.builder()
                .tmp(1D)
                .pop(2D)
                .pty(1)
                .reh(4D)
                .sky(1)
                .tmn(6D)
                .tmx(7D)
                .build();
    }

    @Test
    @DisplayName("기상청 API 반환값 확인")
    void getWeather() {
        Object weather = weatherService.getWeather(initialLocation);
        assertThat(weather).isInstanceOf(Weather.class);
        assertThat(weather).isNotNull();
    }

    @Test
    @DisplayName("비오는 날씨 반환")
    void getWeatherInfo1() {
        initialWeather.setPty(1);
        initialWeather.setSky(1);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF27️");
    }

    @Test
    @DisplayName("비오는 날씨 반환")
    void getWeatherInfo2() {
        initialWeather.setPty(4);
        initialWeather.setSky(3);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF27");
    }

    @Test
    @DisplayName("눈오는 날씨 반환")
    void getWeatherInfo3() {
        initialWeather.setPty(2);
        initialWeather.setSky(3);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF28");
    }

    @Test
    @DisplayName("눈오는 날씨 반환")
    void getWeatherInfo4() {
        initialWeather.setPty(3);
        initialWeather.setSky(1);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF28");
    }


    @Test
    @DisplayName("맑은 날씨 반환")
    void getWeatherInfo5() {
        initialWeather.setPty(0);
        initialWeather.setSky(1);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF1E");
    }

    @Test
    @DisplayName("구름 많은 날씨 반환")
    void getWeatherInfo6() {
        initialWeather.setPty(0);
        initialWeather.setSky(3);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("\uD83C\uDF24");
    }

    @Test
    @DisplayName("흐린 날씨 반환")
    void getWeatherInfo7() {
        initialWeather.setPty(0);
        initialWeather.setSky(4);
        assertThat(weatherService.getWeatherInfo(initialWeather)).contains("☁");
    }
}
