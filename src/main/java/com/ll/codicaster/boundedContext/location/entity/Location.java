package com.ll.codicaster.boundedContext.location.entity;

import com.ll.codicaster.boundedContext.article.entity.Article;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    @OneToOne
    @JoinColumn(name = "article_id")
    private Article article;
    private Double latitude; //gps 로 반환받은 위도
    private Double longitude; //gps 로 반환받은 경도
    private Integer pointX; //x 좌표로 변환된 위도
    private Integer pointY; //y 좌표로 변환된 경도
    @Size(max = 50)
    private String address; //주소 (~동 까지)

    public Location(Double latitude, Double longitude, Point point, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointX = point.getX();
        this.pointY = point.getY();
        this.address = address;
    }

    public static Location getDefaultLocation() {
        return Location.builder()
                .latitude(DefaultLocation.LATITUDE)
                .longitude(DefaultLocation.LONGITUDE)
                .pointX(DefaultLocation.POINT_X)
                .pointY(DefaultLocation.POINT_Y)
                .address(DefaultLocation.ADDRESS)
                .build();
    }
}
