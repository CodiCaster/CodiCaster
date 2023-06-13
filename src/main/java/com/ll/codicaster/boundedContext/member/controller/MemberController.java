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
import com.ll.codicaster.boundedContext.member.entity.Member;
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
        RsData<Void> rsData = memberService.updateMemberInfo(nickname, bodyType, gender);
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return "redirect:/main";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me")
    public String updateMe(@RequestParam String nickname, @RequestParam(required = false) int bodyType,
        @RequestParam String gender) {
        RsData<Void> rsData = memberService.updateMemberInfo(nickname, bodyType, gender);
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return "redirect:/usr/member/me";
    }

    @ResponseBody
    @GetMapping("/checkNickname")
    public RsData<String> checkNickname(@RequestParam String nickname) {
        return memberService.checkNickname(nickname);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public String showMe() {
        RsData<Member> rsData = memberService.getMemberInfo();
        if (rsData.isFail()) {
            return rq.historyBack(rsData.getMsg());
        }
        return "usr/member/me";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteAccount")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String confirmParam = request.getParameter("confirm");
        if (confirmParam != null && confirmParam.equals("false")) {
            return;
        }

        RsData<Void> rsData = memberService.deleteMember();
        if (rsData.isFail()) {
            rq.historyBack(rsData.getMsg());
            return;
        }

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/usr/member/login");
    }
}
