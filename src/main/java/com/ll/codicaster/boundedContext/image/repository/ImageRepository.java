package com.ll.codicaster.boundedContext.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.codicaster.boundedContext.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
