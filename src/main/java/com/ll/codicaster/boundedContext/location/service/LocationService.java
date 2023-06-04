package com.ll.codicaster.boundedContext.location.service;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Point;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final KakaoAPIService kakaoAPIService;
    private final MemberService memberService;

    public RsData<Location> save(LocationDTO locationDTO, Member member) {
        if (locationDTO.getLatitude().isEmpty() || locationDTO.getLongitude().isEmpty()) {
            return RsData.of("F-1", "위치 정보를 불러오지 못했습니다.");
        }

        if (member.getLocationId() != null) {
            return update(locationDTO, member);
        }
        double latitude = Double.parseDouble(locationDTO.getLatitude());
        double longitude = Double.parseDouble(locationDTO.getLongitude());
        Point point = transferToPoint(0, latitude, longitude);
        String address = kakaoAPIService.loadLocationFromKakao(longitude, latitude);
        Location location = new Location(latitude, longitude, point, address);
        locationRepository.save(location);
        memberService.updateLocationId(member.getId(), location.getId());
        return RsData.of("S-1", "위치 정보가 등록되었습니다.", location);
    }

    public RsData<Location> update(LocationDTO locationDTO, Member member) {
        Optional<Location> LocationOptional = locationRepository.findById(member.getLocationId());
        if (!LocationOptional.isPresent()) {
            return RsData.of("F-2", "해당 유저(%s)의 위치 정보가 존재하지 않습니다.".formatted(member.getNickname()));
        }
        Location location = LocationOptional.get();

        double latitude = Double.parseDouble(locationDTO.getLatitude());
        double longitude = Double.parseDouble(locationDTO.getLongitude());
        Point point = transferToPoint(0, latitude, longitude);
        String address = kakaoAPIService.loadLocationFromKakao(longitude, latitude);
        location.update(latitude, longitude, point, address);
        locationRepository.save(location);

        return RsData.of("S-1", "위치 정보가 갱신되었습니다.", location);
    }

    public Location getLocation(Member member) {
        Optional<Location> LocationOptional = locationRepository.findById(member.getLocationId());
        if (!LocationOptional.isPresent()) {
            return null;
        }
        Location location = LocationOptional.get();
        return location;
    }

    //위도, 경도를 x, y 좌표로 변환
    private Point transferToPoint(int mode, double lat, double lon) {
        double xLat = 0;
        double yLon = 0;

        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        if (mode == 0) {
//            rs.lat = lat_X; //gps 좌표 위도
//            rs.lng = lng_Y; //gps 좌표 경도
            double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lon * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            xLat = x;
            yLon = y;
//            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
//            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
//            rs.x = lat_X; //기존의 x좌표
//            rs.y = lng_Y; //기존의 경도
            double xn = xLat - XO;
            double yn = ro - yLon + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                } else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
//            rs.lat = alat * RADDEG; //gps 좌표 위도
//            rs.lng = alon * RADDEG; //gps 좌표 경도
            lat = alat * RADDEG;
            lon = alon * RADDEG;
        }
        return new Point(xLat, yLon);
    }
}
