package com.clinic.service.services;

public interface AuditLogService {
    void logAction(String action, String module, String details);
}
