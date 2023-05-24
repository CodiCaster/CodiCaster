package com.ll.codicaster.boundedContext.home.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm")
@PreAuthorize("hasAuthority('admin')") // admin 권한을 가진 사람만 접근 가능하다는 뜻
public class AdmHomeController {
    @GetMapping("")
    public String showIndex() {
        return "redirect:/adm/home/main";
    }

    @GetMapping("/home/main")
    public String showMain() {
        return "adm/home/main";
    }
}
