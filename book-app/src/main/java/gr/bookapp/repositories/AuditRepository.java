package gr.bookapp.repositories;

import gr.bookapp.common.AuditContext;
import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Audit;

import java.time.Instant;
import java.util.List;

public final class AuditRepository {
    private final Database<Instant, Audit> auditDatabase;
    private final Index<Audit, Long> empoyeeIdIndex = Audit::employeeID;
    private final Index<Audit, String> actionIndex = Audit::action;
    private final RangeIndex<Audit, Instant> timeRangeIndex = RangeIndex.of(Audit::time, Instant::compareTo);

    public AuditRepository(Database<Instant, Audit> auditDatabase) {
        this.auditDatabase = auditDatabase;
    }

    public void add (Audit audit){ auditDatabase.insert(audit.time(), audit);}
    public void audit(long employeeID, String action, Instant time) { auditDatabase.insert(time, new Audit(employeeID, action, time));}

    public Audit getAuditByTime(Instant time){ return auditDatabase.retrieve(time); }

    public List<Audit> getAuditsByTimeRange(Instant from, Instant to){return auditDatabase.findAllInRange(timeRangeIndex, from, to);}

    public List<Audit> getAuditsByEmployeeID(long employeeID){return auditDatabase.findAllByIndex(empoyeeIdIndex, employeeID);}

}
