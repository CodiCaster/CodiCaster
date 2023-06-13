package com.ll.codicaster.base.rq;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.follow.service.FollowService;
import com.ll.codicaster.boundedContext.location.entity.DefaultLocation;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import com.ll.codicaster.boundedContext.notification.service.NotificationService;
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
	private final FollowService followService;

	private final NotificationService notificationService;
	private final MessageSource messageSource;
	private final LocaleResolver localeResolver;
	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final HttpSession session;
	private final User user;
	private Locale locale;
	private Member member = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.

	public Rq(MemberService memberService, LocationService locationService, WeatherService weatherService,
		FollowService followservice, NotificationService notificationService, MessageSource messageSource,
		LocaleResolver localeResolver,
		HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		this.memberService = memberService;
		this.locationService = locationService;
		this.weatherService = weatherService;
		this.followService = followservice;
		this.notificationService = notificationService;
		this.messageSource = messageSource;
		this.localeResolver = localeResolver;
		this.req = req;
		this.resp = resp;
		this.session = session;

		// 현재 로그인한 회원의 인증정보를 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication.getPrincipal() instanceof User) {
			this.user = (User)authentication.getPrincipal();
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
            member = memberService.findByUsername(user.getUsername()).orElse(null);
        }

		return member;
	}

	public String getNickname() {
		Member member = getMember();
		if (member != null) {
			return member.getNickname();
		}
		return null;
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
		SavedRequest savedRequest = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

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


    public String getAddress() {
        return getCurrentLocation().getAddress();
    }

    private Location setSessionLocationDefault() {
        Location location = Location.getDefaultLocation();
        session.setAttribute("location", Location.getDefaultLocation());
        return location;
    }

	public void setLocation(Location location) {
		session.setAttribute("location", location);
	}

	public String getCurrentDate() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd(E)", Locale.KOREAN);
		return currentDate.format(formatter);
	}

    public Location getCurrentLocation() {
        Location location = (Location) session.getAttribute("location");
        if (location == null) {
            return setSessionLocationDefault();
        }
        return location;
    }

	public String getWeatherInfo() {
		Location location = getCurrentLocation();
		return weatherService.getWeatherInfo(location);
	}

	public boolean isFollowed(Member followee) {
		Member user = getMember();
		List<Member> followingMembers = followService.getFollowingMembers(user);
		return followingMembers.contains(followee);
	}

	public boolean hasUnreadNotifications() {
		if (isLogout())
			return false;

		Member user = getMember();

		return notificationService.countUnreadNotificationsByReceiver(user);
	}


    public String getParamsJsonStr() {
        Map<String, String[]> parameterMap = req.getParameterMap();
        return Ut.json.toStr(parameterMap);
    }
	//일교차 높은지 확인
	public boolean hasHighTemperatureDifference() {
		double tmn = weatherService.getWeather(getCurrentLocation()).getTmn(); // 일 최저기온
		double tmx = weatherService.getWeather(getCurrentLocation()).getTmx(); // 일 최고기온
		double temperatureDifference = tmx - tmn; // 일교차
		return temperatureDifference >= 15.0;
	}

	public List<Member> getFollwingMembers() {
		return followService.getFollowingMembers(getMember());
	}

	public List<Member> getFollowers (){return followService.getFollowers(getMember());}
}
