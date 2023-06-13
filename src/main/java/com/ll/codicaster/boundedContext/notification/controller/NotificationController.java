package com.ll.codicaster.boundedContext.notification.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.notification.service.NotificationService;
import com.ll.codicaster.boundedContext.notification.entity.Notification;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
	private final Rq rq;
	private final NotificationService notificationService;

	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public String showList(Model model) {
		List<Notification> notifications = notificationService.findByReceiver(rq.getMember());
		notificationService.markAsRead(notifications);

		model.addAttribute("notifications", notifications);

		return "usr/notification/list";
	}
}
