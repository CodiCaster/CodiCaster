package com.ll.codicaster.boundedContext.region.controller;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.region.dto.LocationDTO;
import com.ll.codicaster.boundedContext.region.service.RegionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/usr/location")
public class RegionController {
    private final RegionService regionService;

    //위치 정보 갱신 버튼을 누르면 위치 갱신
    @PostMapping("/save")
    public String saveRegion(LocationDTO locationDTO, Member member) {
        if (locationDTO.getLatitude().isEmpty() || locationDTO.getLongitude().isEmpty()) {
            return "error/404";
        }
        regionService.save(locationDTO, member);
        return "usr/member/me";
    }
}
