package com.ll.codicaster.boundedContext.weather.entity;

import com.ll.codicaster.boundedContext.article.entity.Article;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@SuperBuilder
public class Weather {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "article_id")
    private Article article;
    private Double tmp;     //현재 기온
    private Double pop;     //강수 확률
    private Integer pty;    //강수 형태 0 ~ 4
    //없음 : 0, 비 : 1, 비/눈 : 2, 눈 : 3, 소나기 : 4
    private Double reh;     //습도
    private Integer sky;    //하늘 상태 1, 3, 4
    // 맑음 : 1, 구름 많음 : 3, 흐림 : 4
    private Double tmn;     //일 최저기온
    private Double tmx;     //일 최고기온
}
