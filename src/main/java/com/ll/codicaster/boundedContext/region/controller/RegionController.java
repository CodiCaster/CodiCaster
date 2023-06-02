package com.ll.codicaster.boundedContext.region.controller;


import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.region.dto.LocationDTO;
import com.ll.codicaster.boundedContext.region.entity.Region;
import com.ll.codicaster.boundedContext.region.service.RegionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/usr/location")
public class RegionController {
    private final Rq rq;
    private final RegionService regionService;

    //위치 정보 갱신 버튼을 누르면 위치 갱신
    @PostMapping("/save")
    public String saveRegion(LocationDTO locationDTO) {
        RsData<Region> rsData = regionService.save(locationDTO, rq.getMember());
        if (rsData.isFail()) {
//            return "error/404";
            return rq.historyBack(rsData);
        }
//        return "error/404";
        return rq.redirectWithMsg("usr/member/me", rsData);
    }
}
