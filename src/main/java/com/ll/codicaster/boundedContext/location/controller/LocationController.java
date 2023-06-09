package com.ll.codicaster.boundedContext.location.controller;


import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.location.dto.LocationDTO;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/usr/location")
public class LocationController {
    private final Rq rq;
    private final LocationService locationService;

    @PostMapping("/update/main")
    public String updateMain(LocationDTO locationDTO) {
        RsData<Location> rsData = locationService.getCurrentLocation(locationDTO);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        Location location = rsData.getData();
        rq.setLocation(location);
        return "redirect:/main";
    }

    @PostMapping("/update/me")
    public String updateMe(LocationDTO locationDTO) {
        RsData<Location> rsData = locationService.getCurrentLocation(locationDTO);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        Location location = rsData.getData();
        rq.setLocation(location);
        return "redirect:/usr/member/me";
    }
}
