package com.ll.codicaster.boundedContext.location.service;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.DefaultLocation;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.repository.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LocationServiceTest {

    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;
    private LocationDTO locationDTO;


    @Test
    @DisplayName("이벤트 리스너 동작 확인")
    void whenAfterWriteTest() {
        Location location = Location.getDefaultLocation();
        Article article = new Article();

        locationService.whenAfterWrite(location, article);

        Optional<Location> savedLocation = locationRepository.findById(1L);
        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.get()).isInstanceOf(Location.class);
        assertThat(savedLocation.get().getLongitude()).isEqualTo(DefaultLocation.LONGITUDE);
        assertThat(savedLocation.get().getLatitude()).isEqualTo(DefaultLocation.LATITUDE);
        assertThat(savedLocation.get().getPointX()).isEqualTo(DefaultLocation.POINT_X);
        assertThat(savedLocation.get().getPointY()).isEqualTo(DefaultLocation.POINT_Y);
        assertThat(savedLocation.get().getAddress()).isEqualTo(DefaultLocation.ADDRESS);
    }

    @Test
    @DisplayName("Location 객체 생성 및 저장 확인")
    void getCurrentLocationTest() {
        Double latitude = DefaultLocation.LATITUDE;
        Double longitude = DefaultLocation.LONGITUDE;

        locationDTO = new LocationDTO();
        locationDTO.setLatitude("" + latitude);
        locationDTO.setLongitude("" + longitude);

        RsData<Location> rsData = locationService.getCurrentLocation(locationDTO);
        Location location = rsData.getData();

        assertThat(location.getLatitude()).isEqualTo(latitude);
        assertThat(location.getLongitude()).isEqualTo(longitude);
        assertThat(location.getAddress()).contains("경기도 성남시 분당구");
    }
}