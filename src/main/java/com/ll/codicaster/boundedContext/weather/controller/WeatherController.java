package com.ll.codicaster.boundedContext.weather.controller;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.entity.Point;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/usr/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final LocationService locationService;
    private final Rq rq;


    public RsData<Weather> save(Member member) throws IOException {

        Location location = locationService.getLocation(member.getLocationId());
        weatherService.save(location);
        memberService.updateLocationId(member.getId(), location.getId());
        return RsData.of("S-1", "위치 정보가 등록되었습니다.", location);
    }

    public Weather getWeather(Member member) throws IOException {
        Location location = locationService.getLocation(member.getLocationId());
        Weather weather = weatherService.getWeather(location);

    }
}
