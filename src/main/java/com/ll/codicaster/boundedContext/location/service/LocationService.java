package com.ll.codicaster.boundedContext.location.service;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Point;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.repository.LocationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final LocationRepository locationRepository;
    private final KakaoAPIService kakaoAPIService;

    public void whenAfterWrite(Location location, Article article) {
        Location newLocation = Location.builder()
                .article(article)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .pointX(location.getPointX())
                .pointY(location.getPointY())
                .address(location.getAddress())
                .build();
        locationRepository.save(newLocation);
    }

    public RsData<Location> getCurrentLocation(LocationDTO locationDTO) {
        if (locationDTO.getLatitude().isEmpty() || locationDTO.getLongitude().isEmpty()) {
            return RsData.of("F-1", "위치 정보를 불러오지 못했습니다.");
        }

        double latitude = Double.parseDouble(locationDTO.getLatitude());
        double longitude = Double.parseDouble(locationDTO.getLongitude());
        Point point = transferToPoint(latitude, longitude);
        String address = kakaoAPIService.getAddressFromKakao(longitude, latitude);
        if (address == null) {
            return RsData.of("F-2", "위치 정보를 불러오지 못했습니다.");
        }
        Location location = new Location(latitude, longitude, point, address);

        return RsData.of("S-1", "현재 위치 정보가 갱신되었습니다.", location);
    }

    //위도, 경도를 x, y 좌표로 변환
    private Point transferToPoint(double lat, double lon) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double tan = Math.tan(Math.PI * 0.25 + slat1 * 0.5);

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / tan;
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = tan;
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return new Point(x, y);
    }
}
