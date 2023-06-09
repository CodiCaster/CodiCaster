package com.ll.codicaster.boundedContext.follow;

import com.ll.codicaster.base.baseEntity.BaseEntity;
import com.ll.codicaster.boundedContext.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@AllArgsConstructor
@Builder
public class Follow extends BaseEntity {

	@ManyToOne
	private Member follower;
	@ManyToOne
	private Member followee;

}