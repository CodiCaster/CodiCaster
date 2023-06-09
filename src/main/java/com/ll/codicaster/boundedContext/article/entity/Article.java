package com.ll.codicaster.boundedContext.article.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.member.entity.Member;

import com.ll.codicaster.boundedContext.weather.entity.Weather;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @OneToOne
    private Member author;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
    private Location location;
    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
    private Weather weather;
    private String address;
    private String weatherInfo;

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
    private Image image;

    @ElementCollection
    private Set<String> tagSet;

    @ManyToMany
    @JoinTable(
            name = "article_likes",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likedMembers = new HashSet<>();

    public int getLikesCount() {
        return likedMembers.size();
    }

}