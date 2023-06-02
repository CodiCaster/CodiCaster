package com.ll.codicaster.boundedContext.region.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Double latitude; //gps 로 반환받은 위도
    private Double longitude; //gps 로 반환받은 경도
    private Integer pointX; //x 좌표로 변환된 위도
    private Integer pointY; //y 좌표로 변환된 경도
    private String address; //주소 (~동 까지)

    public Region(Double latitude, Double longitude, Point point, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
    }

    public void update(Double latitude, Double longitude, Point point, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
    }
}
