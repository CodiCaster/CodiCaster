package com.ll.codicaster.boundedContext.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long articleId;
    private Double latitude; //gps 로 반환받은 위도
    private Double longitude; //gps 로 반환받은 경도
    private Integer pointX; //x 좌표로 변환된 위도
    private Integer pointY; //y 좌표로 변환된 경도
    private String address; //주소 (~동 까지)

    public Location(Double latitude, Double longitude, Point point, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
    }

    public Location(Double latitude, Double longitude, Integer pointX, Integer pointY, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = pointX;
        this.pointY = pointY;
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
