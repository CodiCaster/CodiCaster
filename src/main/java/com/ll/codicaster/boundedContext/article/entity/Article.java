package com.ll.codicaster.boundedContext.article.entity;

import java.util.HashSet;
import java.util.Set;

import com.ll.codicaster.base.baseEntity.BaseEntity;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.member.entity.Member;

import com.ll.codicaster.boundedContext.weather.entity.Weather;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Article extends BaseEntity {
    @Column(length = 60)
    private String title;
    @Column(length = 600)
    private String content;
    @OneToOne
    private Member author;
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

    public boolean isLiked(Member member) {
        return this.likedMembers.contains(member);
    }

    public void addLikeMember(Member member) {
        this.likedMembers.add(member);
    }
}