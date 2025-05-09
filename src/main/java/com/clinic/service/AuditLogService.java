package com.clinic.service;
import com.clinic.entity.AuditLog;

public interface AuditLogService {
    void logAction(String action, String module, String details);
}
