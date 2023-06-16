# 🌦️ 코디 캐스터 (Codi Caster)

> 개발기간: 2022.05 - 2022.06 (4주)  
> 프로젝트 유형: 4인 팀 프로젝트
</br>

## 서비스 소개

- 코디캐스터(CodiCaster) = 의상을 균형있게 갖추다 + 전문성있게 정보를 전달하는사람
- 사용자의 위치와 날씨 등에 따른 옷차림을 추천해주는 SNS 성격의 웹 서비스

</br>

## 개발 배경

- 날씨 정보만으로는 어느 정도의 의상이 적절한지 판단하기 어려움
- 근처에 사는 사람들과 나와의 체질이 비슷한 사람들은 해당 날씨에 어떻게 입는지 궁금함
> **근처에 위치하고, 나와 비슷한 체질을 가진 게시글들을 추천해주면서 날씨에 따른 옷차림 고민을 덜어주고자 기획**

</br>

## 기대 효과

- 과거 본인의 의상 사진과 남긴 후기를 보면 의상 선정에 있어 도움이 될 수 있음
- 옷차림 선정 시 근처 사람들의 옷차림을 참고하여 고민하는 시간을 덜어줌
- 나의 체질(추위, 더위를 많이 타는지 등)에 따른 옷차림 추천으로 만족도가 높을 수 있음

</br>

## 팀원 소개 및 역할

- **조희권**: BE, 1주차 PM

- **신희수**: BE, 2주차 PM

- **박다원**: BE, 3주차 PM

- **송호준**: BE, 4주차 PM

## 기술 스택

### Front-End

<img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"> <img src="https://img.shields.io/badge/tailwindcss-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white">
<img src="https://img.shields.io/badge/daisyui-5A0EF8?style=for-the-badge&logo=daisyui&logoColor=white">
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">

그 외: TOAST UI
### Back-End

<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
<img src="https://img.shields.io/badge/Springsecurity-3CB371?style=for-the-badge&logo=Springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/JPA-808080.svg?style=for-the-badge&logo=Hibernate&logoColor=white">
<img src="https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=mariadb&logoColor=white">

그 외: 기상청 API, 카카오 위치 API, GeoLocation, Spring Oauth 2.0, 네이버 Object Storage, 네이버 클라우드 플랫폼
### Deployment

<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/nginx-%23009639?style=for-the-badge&logo=nginx&logoColor=white">

### Editor

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

### Communication

![github](https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logo=github&logoColor=white) 
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
![discord](https://img.shields.io/badge/discord-5865F2.svg?style=for-the-badge&logo=discord&logoColor=white) 

### ETC

- **Authentication**: 카카오, 네이버 로그인 API

- **Image Processing**: 네이버 Image Optimizer, CDN+
</br>

## ERD

![image](https://github.com/CodiCaster/CodiCaster/assets/117694148/0164bee8-e9a0-465d-a7a4-1db7c9dc0388)

## 서비스 설명 및 주요 기능

#### 회원가입
1. 소셜 로그인(카카오, 네이버)
  - 회원가입 대체
2. 기본 정보 입력
  - 닉네임, 성별, 체질(추위 ~ 더위 많이 타는 순으로 5개 중 선택)

#### 마이페이지
1. 내 정보 확인 (+ 위치, 날씨)
  - 기본 정보 수정
2. 내가 작성한 게시물 모아보기
3. 팔로잉, 팔로워 확인

#### 게시물
1. 기본적인 CRUD
  - 게시물에는 오늘 나의 옷차림을 업로드할 수 있음
  - 해시태그 기능 (ex. #반팔 #반바지)
2. 상세 게시물
  - 좋아요 기능
  - 팔로우 기능
  - 게시글과 함께 업로드 위치와 날씨 확인 가능
3. 관심 게시물
  - 사용자가 팔로우한 게시물 모아보기

### 메인 홈
#### 추천 알고리즘 - 비로그인
> 보유 정보: 위치, 날짜
1. 날짜 기준 필터링
  - 같은 시즌에는 비슷한 유형의 의류를 착용할 확률 ⬆️
  - 날짜가 가까울 수록 원하는 옷차림을 찾을 확률 ⬆️
2. 거리순으로 정렬
  - 거리가 가까우면 날씨가 비슷할 확률 ⬆️
  - 유행은 지역의 영향도 받으므로, 근처 사람들의 옷차림 확인 가능

#### 추천 알고리즘 - 로그인
> 보유 정보: 위치, 날짜, 체질, 성별
1. 성별 필터링
  - 코디 추천의 관점에서 성별이 다른 게시물은 무의미
    - 전체 게시물, 팔로우 게시물에서 타 성별 게시물 확인 가능
2. 위치 가산점 부여
  - 비로그인의 근거와 동일
    - 10km 이내에 업로드된 게시물에 +1 가산점 부여
3. 체질 가산점 부여
  - 체질이 다른 사람과는 비슷한 착장을 기대하기 어려움
  - 체질이 비슷한 사람의 게시물에 가산점 부여
    - 사용자의 체질과 오차가 1 이하인 게시물에 +1 가산점 부여
4. 태그 가산점 부여
  - *추가 정보: 태그*
  - 사용자가 게시물을 작성할 때 등록했던 태그 활용
    - 태그를 통해 회원의 성향을 대략적으로 파악
    - 사용자가 사용한 태그가 게시물에 포함되어 있다면 태그 하나당 +0.5 가산점 부여
5. 좋아요 가산점 부여 
  - *추가 정보: 좋아요*
  - 인기 게시물은 많은 사람이 좋아요를 누른 만큼 추천받을 만한 착장일 확률 ⬆️
    - 날씨, 위치보다 영향력이 낮아야 하기 때문에 좋아요 하나당 +0.01 가산점 부여
</br>

## 협업


[🌐 그라운드 룰](https://www.notion.so/daxx0ne/20f1762deb2940aab9934da55bbdba61?pvs=4)

[📙 커밋 컨벤션](https://www.notion.so/daxx0ne/fbf3d4d07f2242579a5ef7d912dd9ee8?pvs=4)
