package com.ll.codicaster.boundedContext.article.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.repository.ArticleRepository;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.image.repository.ImageRepository;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final LocationService locationService;
    private final WeatherService weatherService;
    private final ImageRepository imageRepository;

    private final Rq rq;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public static Set<String> extractHashTagList(String content) {
        Set<String> tagSet = new HashSet<>();

        Pattern pattern = Pattern.compile("#([ㄱ-ㅎ가-힣a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String tag = matcher.group(1);
            tagSet.add(tag);
        }

        return tagSet;
    }

    public RsData<Article> saveArticle(Member actor, ArticleCreateForm form, MultipartFile imageFile) {

        Set<String> tagSet = extractHashTagList(form.getContent());
        updateUserTagMap(actor, tagSet);


        Location location = rq.getCurrentLocation();
        Long locationId = locationService.save(location);
        Long weatherId = weatherService.save(location);

        Article article = Article.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .author(actor)
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .tagSet(tagSet)
                .locationId(locationId)
                .weatherId(weatherId)
                .build();

        articleRepository.save(article);

        // 이미지 파일이 있으면 저장
        if (!imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + imageFile.getOriginalFilename();

            File directory = new File(uploadDir);
            // 디렉토리가 존재하지 않으면 생성
            if (!directory.exists()) {
                directory.mkdirs(); // 상위 디렉토리까지 모두 생성
            }

            File saveFile = new File(uploadDir, fileName);
            try {
                imageFile.transferTo(saveFile);
            } catch (Exception e) {
                return RsData.of("F-4", "이미지 업로드에 실패하였습니다");
            }

            Image image = new Image();
            image.setFilename(fileName);
            image.setFilepath("/images/" + fileName);
            image.setArticle(article);  // Image 객체와 Article 객체를 연결

            image = imageRepository.save(image);  // 이미지를 DB에 저장

            article.setImage(image); // 이미지 정보를 게시글에 추가
        }

        return RsData.of("S-1", "성공적으로 저장되었습니다", article);

    }

    //게시물 전체 리스트
    public List<Article> articleList() {
        return articleRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Article::getId).reversed())
                .collect(Collectors.toList());
    }

    //게시물 상세
    public Article articleDetail(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));
    }

    //게시물 수정
    @Transactional
    public boolean updateArticle(Long id, ArticleCreateForm form, MultipartFile imageFile) {
        try {
            Article article = articleRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));

            // 게시글의 정보를 수정
            article.setTitle(form.getTitle());
            article.setContent(form.getContent());
            article.setModifyDate(LocalDateTime.now());

            // 이미지 파일이 있으면 저장
            if (!imageFile.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + imageFile.getOriginalFilename();

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File saveFile = new File(uploadDir, fileName);
                imageFile.transferTo(saveFile);

                // 기존 이미지가 있으면 삭제
                Image oldImage = article.getImage();
                if (oldImage != null) {
                    // 실제 파일 삭제
                    File oldFile = new File(uploadDir, oldImage.getFilename());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }

                    // DB에서 기존 이미지 삭제
                    imageRepository.delete(oldImage);
                }

                // 새 이미지 정보를 설정하고 저장
                Image image = new Image();
                image.setFilename(fileName);
                image.setFilepath("/images/" + fileName);
                image.setArticle(article);

                image = imageRepository.save(image);

                article.setImage(image);
            }

            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteArticle(Long id) {
        //RsData 사용 필요해보임
        Article article = articleRepository.findById(id).get();
        Long locationId = article.getLocationId();
        Long weatherId = article.getWeatherId();

        try {
            locationService.delete(locationId);
            weatherService.deleteById(weatherId);
            articleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

    public String getAddress(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No Article Found with id: " + id));
        return locationService.getLocation(article.getLocationId()).getAddress();
    }

    public String getTemperature(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No Article Found with id: " + id));
        return weatherService.getWeather(article.getLocationId()).getTmp() + "°C";
    }

    public Article findArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));
    }

    //일년전 게시물 조회
    public List<Article> getArticlesYearAgo() {
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1); // 오늘로부터 1년 전
        LocalDate oneYearAndMonthAgo = oneYearAgo.minusMonths(1); // 오늘로부터 1년 전 - 한달
        LocalDate oneYearPlusMonthAgo = oneYearAgo.plusMonths(1);// 오늘로부터 1년 전 + 한달
        LocalDate startDate = oneYearAndMonthAgo; // 오늘로부터 1년 전 ± 한달
        LocalDate endDate = oneYearPlusMonthAgo;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return articleRepository.findByCreateDateBetween(startDateTime, endDateTime);
    }

    //한달전 ~ 현재 게시물 조회
    public List<Article> getArticlesLastOneMonth() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1); //한달전

        LocalDateTime startDateTime = oneMonthAgo.atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();

        return articleRepository.findByCreateDateBetween(startDateTime, endDateTime);
    }

    //일년전 오늘 앞뒤 한달 + 한달전 ~ 오늘 게시물 조회
    public List<Article> showArticlesNearbyToday() {
        List<Article> articleLastOneMonth = getArticlesLastOneMonth();
        List<Article> articleYearAgo = getArticlesYearAgo();
        //일년전의 게시물도 포함되었으므로 최신순 정렬기능은 넣지 않는다.
        List<Article> ArticlesNearbyToday = Stream.concat(articleYearAgo.stream(), articleLastOneMonth.stream())
                .collect(Collectors.toList());

        return ArticlesNearbyToday;

    }

    //유저 태그맵 업데이트 (게시물 작성 시마다 태그리스트 받아서 가지고 있는지 확인하고 증가)
    public void updateUserTagMap(Member member, Set<String> tagSet) {
        Map<String, Integer> tagMap = member.getTagMap();

        for (String tag : tagSet) {
            tagMap.put(tag, tagMap.getOrDefault(tag, 0) + 1);
        }

    }

}
