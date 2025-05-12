package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.storage.codec.Audit;

import java.time.Instant;
import java.util.List;

public final class AuditRepositoryDbImpl implements AuditRepository {
    private final Database<Instant, Audit> auditDatabase;
    private final Index<Audit, Long> empoyeeIdIndex = Audit::userID;
    private final Index<Audit, String> actionIndex = Audit::action;
    private final RangeIndex<Audit, Instant> timeRangeIndex = RangeIndex.of(Audit::time, Instant::compareTo);

    public AuditRepositoryDbImpl(Database<Instant, Audit> auditDatabase) {
        this.auditDatabase = auditDatabase;
    }

    @Override
    public void add(Audit audit){ auditDatabase.insert(audit.time(), audit);}
    @Override
    public void audit(long employeeID, String action, Instant time) { auditDatabase.insert(time, new Audit(employeeID, action, time));}

    @Override
    public Audit getAuditByTime(Instant time){ return auditDatabase.retrieve(time); }

    @Override
    public List<Audit> getAuditsByTimeRange(Instant from, Instant to){return auditDatabase.findAllInRange(timeRangeIndex, from, to);}

    @Override
    public List<Audit> getAuditsByEmployeeID(long employeeID){return auditDatabase.findAllByIndex(empoyeeIdIndex, employeeID);}

}
