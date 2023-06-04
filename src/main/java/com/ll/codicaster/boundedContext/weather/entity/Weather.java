package com.ll.codicaster.boundedContext.weather.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Double tmp;     // 온도 TMP
    private Double pop;     // 강수 확률
    private Integer pty;    //강수 형태 0 ~ 4
                            //없음 : 0, 비 : 1, 비/눈 : 2, 눈 : 3, 소나기 : 4
    private Double reh;     //습도
    private Integer sky;        //하늘 상태 0 ~ 10
                            // 맑음 : 0 ~ 5, 구름 많음 : 6 ~ 8, 흐림 : 9 ~ 10
    private Double tmn;     //일 최저기온  17.0
    private Double tmx;     //일 최고기온  28.0
    private Integer parent;     // 값 : 0 또는 1
                            //0일 때 게시글에 해당하는 날씨 정보
                            //1일 때 멤버에 해당하는 날씨 정보
}
