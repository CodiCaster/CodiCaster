package com.ll.codicaster.base.baseEntity;

import static jakarta.persistence.GenerationType.*;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
public abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;
}