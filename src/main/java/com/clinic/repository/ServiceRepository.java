package com.clinic.repository;

import com.clinic.entity.billing.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsByNameIgnoreCase(String name);
}
