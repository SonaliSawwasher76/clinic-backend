package com.clinic.service.impl;

import com.clinic.entity.AuditLog;
import com.clinic.repository.AuditLogRepository;

import com.clinic.service.services.AuditLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logAction(String action, String module, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setModule(module);
        log.setPerformedBy("System"); // Or fetch from user context later
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);

        auditLogRepository.save(log);
    }
}
