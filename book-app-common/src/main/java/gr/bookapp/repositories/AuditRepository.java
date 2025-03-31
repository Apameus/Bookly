package gr.bookapp.repositories;

import gr.bookapp.models.Audit;

import java.time.Instant;
import java.util.List;

public interface AuditRepository {
    void add(Audit audit);

    void audit(long employeeID, String action, Instant time);

    Audit getAuditByTime(Instant time);

    List<Audit> getAuditsByTimeRange(Instant from, Instant to);

    List<Audit> getAuditsByEmployeeID(long employeeID);
}
