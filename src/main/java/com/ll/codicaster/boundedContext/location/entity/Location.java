package com.ll.codicaster.boundedContext.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Double latitude; //gps 로 반환받은 위도
    private Double longitude; //gps 로 반환받은 경도
    private Integer pointX; //x 좌표로 변환된 위도
    private Integer pointY; //y 좌표로 변환된 경도
    private String address; //주소 (~동 까지)
    private Integer parent;     // 값 : 0 또는 1
    //0일 때 게시글에 해당하는 날씨 정보
    //1일 때 멤버에 해당하는 날씨 정보

    public Location(Double latitude, Double longitude, Point point, String address, int parent) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
        this.parent = parent;
    }

    public void update(Double latitude, Double longitude, Point point, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
    }
}
