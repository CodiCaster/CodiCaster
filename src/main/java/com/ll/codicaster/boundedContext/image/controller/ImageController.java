package com.ll.codicaster.boundedContext.image.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ImageController {
	// @GetMapping("/images/{filename}")
	// public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
	// 	try {
	// 		Path filepath = Paths.get("/your/upload/directory/" + filename);
	// 		Resource resource = new UrlResource(filepath.toUri());
	// 		if (resource.exists() || resource.isReadable()) {
	// 			return ResponseEntity.ok().body(resource);
	// 		} else {
	// 			// 파일이 없거나 읽을 수 없는 경우의 처리
	// 		}
	// 	} catch (MalformedURLException e) {
	// 		e.printStackTrace();
	// 		// 예외 처리 로직 추가
	// 	}
	// 	return ResponseEntity.notFound().build();
	// }
}
