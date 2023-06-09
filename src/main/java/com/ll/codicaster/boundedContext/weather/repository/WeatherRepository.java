package com.ll.codicaster.boundedContext.weather.repository;

import com.ll.codicaster.boundedContext.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    void deleteByArticleId(Long articleId);
}
