package com.ll.codicaster.boundedContext.location.controller;


import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/usr/location")
public class LocationController {
    private final Rq rq;
    private final LocationService locationService;

    //위치 정보 갱신 버튼을 누르면 위치 갱신
    @PostMapping("/save")
    public String saveLocation(LocationDTO locationDTO) {
        RsData<Location> rsData = locationService.save(locationDTO, rq.getMember());
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return "redirect:/usr/member/me";
    }
}
