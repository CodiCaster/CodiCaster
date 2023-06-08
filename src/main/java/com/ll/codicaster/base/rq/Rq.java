package com.ll.codicaster.base.rq;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.location.entity.LocationConstants;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import com.ll.codicaster.standard.util.Ut;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
@RequestScope
public class Rq {
    private final MemberService memberService;
    private final LocationService locationService;
    private final WeatherService weatherService;

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private Locale locale;
    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    private final HttpSession session;
    private final User user;
    private Member member = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.


    public Rq(MemberService memberService, LocationService locationService, WeatherService weatherService, MessageSource messageSource, LocaleResolver localeResolver,
              HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        this.memberService = memberService;
        this.locationService = locationService;
        this.weatherService = weatherService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.req = req;
        this.resp = resp;
        this.session = session;


        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    // 로그인 되어 있는지 체크
    public boolean isLogin() {
        return user != null;
    }

    // 로그아웃 되어 있는지 체크
    public boolean isLogout() {
        return !isLogin();
    }

    // 로그인 된 회원의 객체
    public Member getMember() {
        if (isLogout())
            return null;

        // 데이터가 없는지 체크
        if (member == null) {
            member = memberService.findByUsername(user.getUsername()).orElseThrow();
        }

        return member;
    }

    public String getAddress() {
        Location location = (Location) session.getAttribute("location");
        if (location == null) {
            location = new Location(LocationConstants.LATITUDE, LocationConstants.LONGITUDE,
                    LocationConstants.POINT_X, LocationConstants.POINT_Y, LocationConstants.ADDRESS);
            setLocation(location);
        }
        return location.getAddress();
    }

    public String getDateAndWeather() {
        Location location = (Location) session.getAttribute("location");
        Weather weather = weatherService.getWeather(location);
        return getCurrentDate() + " " + weather.getTmp() + "°C";
    }

    public String getTemperature() {
        Location location = (Location) session.getAttribute("location");
        Weather weather = weatherService.getWeather(location);
        return weather.getTmp() + "°C";
    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd(E)", Locale.KOREAN);
        return currentDate.format(formatter);
    }

    public String getCText(String code, String... args) {
        return messageSource.getMessage(code, args, getLocale());
    }

    private Locale getLocale() {
        if (locale == null)
            locale = localeResolver.resolveLocale(req);
        return locale;
    }

    public Long getLoginedMemberId() {
        return getMember().getId();
    }

    public boolean isRefererAdminPage() {
        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (savedRequest == null)
            return false;

        String referer = savedRequest.getRedirectUrl();
        return referer != null && referer.contains("/adm");
    }

    // 뒤로가기 + 메세지
    public String historyBack(String msg) {
        String referer = req.getHeader("referer");
        String key = "historyBackErrorMsg___" + referer;
        req.setAttribute("localStorageKeyAboutHistoryBackErrorMsg", key);
        req.setAttribute("historyBackErrorMsg", msg);
        // 200 이 아니라 400 으로 응답코드가 지정되도록
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "common/js";
    }

    // 뒤로가기 + 메세지
    public String historyBack(RsData rsData) {
        return historyBack(rsData.getMsg());
    }

    // 302 + 메세지
    public String redirectWithMsg(String url, RsData rsData) {
        return redirectWithMsg(url, rsData.getMsg());
    }

    // 302 + 메세지
    public String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }

    private String urlWithMsg(String url, String msg) {
        // 기존 URL에 혹시 msg 파라미터가 있다면 그것을 지우고 새로 넣는다.
        return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    // 메세지에 ttl 적용
    private String msgWithTtl(String msg) {
        return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

    public void setLocation(Location location) {
        session.setAttribute("location", location);
    }

    public HttpSession getSession() {
        return this.session;
    }

    public Location getCurrentLocation() {
        Location location = (Location) session.getAttribute("location");
        if (location == null) {
            location = new Location(LocationConstants.LATITUDE, LocationConstants.LONGITUDE,
                    LocationConstants.POINT_X, LocationConstants.POINT_Y, LocationConstants.ADDRESS);
            setLocation(location);
        }
        return location;
    }

    public String getWeatherInfo() {
        Location location = (Location) session.getAttribute("location");
        return weatherService.getWeatherInfo(location);
    }
}
