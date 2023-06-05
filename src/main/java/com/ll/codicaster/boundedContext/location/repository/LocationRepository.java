package com.ll.codicaster.boundedContext.location.repository;

import com.ll.codicaster.boundedContext.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
