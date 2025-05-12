package gr.bookapp.services;

import gr.bookapp.storage.codec.Audit;
import gr.bookapp.common.AuditContext;
import gr.bookapp.repositories.AuditRepository;

import java.time.Clock;

public final class AuditService {
    private final AuditRepository auditRepository;
    private final AuditContext auditContext;
    private final Clock clock;

    public AuditService(AuditRepository auditRepository, AuditContext auditContext) {
        this.auditRepository = auditRepository;
        this.auditContext = auditContext;
        clock = Clock.systemUTC();
    }

    public void audit(String action){
        Audit audit = new Audit(auditContext.getUserID(), action, clock.instant());
        auditRepository.add(audit);
    }
}
