package com.ll.codicaster.boundedContext.member.controller;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

	@PreAuthorize("isAnonymous()")
	@GetMapping("/login")
	public String showLogin() {
		return "usr/member/login";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/newInfo")
	public String showNewInfo() {
		return "usr/member/newInfo";
	}


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/newInfo")
    public String updateInfo(@RequestParam String nickname, @RequestParam(required = false) int bodyType,
                             @RequestParam String gender) {
        memberService.updateMemberInfo(rq.getLoginedMemberId(), nickname, bodyType, gender);
        return "redirect:/main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me")
    public String updateMe(@RequestParam String nickname, @RequestParam(required = false) int bodyType,
        @RequestParam String gender) {
        memberService.updateMemberInfo(rq.getLoginedMemberId(), nickname, bodyType, gender);
        return "redirect:/usr/member/me";
    }

    @ResponseBody
    @GetMapping("/checkNickname")
    public RsData<String> checkNickname(@RequestParam String nickname) {
        return memberService.checkNickname(nickname);
    }

    @PreAuthorize("isAuthenticated()") // 로그인 해야만 접속가능
    @GetMapping("/me") // 로그인 한 나의 정보 보여주는 페이지
    public String showMe() {
        if(rq.isLogout()){
            return "usr/member/login";
        }
        if (rq.getNickname() == null) {
            return "usr/member/login";
        }
        return "usr/member/me";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteAccount")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        // Confirm delete dialog에서 취소를 선택한 경우
        String confirmParam = request.getParameter("confirm");
        if (confirmParam != null && confirmParam.equals("false")) {
            // 아무 작업 없이 메서드 종료
            return;
        }

        // Confirm delete dialog에서 확인을 선택한 경우에만 회원 탈퇴 작업 수행
        memberService.deleteMember(rq.getLoginedMemberId());
        SecurityContextHolder.clearContext();
        try {
            request.getSession().invalidate();
            response.sendRedirect("/usr/member/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
