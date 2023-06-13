package com.ll.codicaster.boundedContext.follow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.codicaster.boundedContext.follow.entity.Follow;
import com.ll.codicaster.boundedContext.member.entity.Member;

public interface FollowRepository extends JpaRepository <Follow, Long> {

	Follow findByFollowerAndFollowee(Member follower, Member followee);

	List<Follow> findByFollower(Member follower);

	List<Follow> findByFollowee(Member followee);
}
