package gr.bookapp.common;

public final class AuditContextImp implements AuditContext{
    private static final ThreadLocal<Long> currentEmployeeID = new ThreadLocal<>();

    public static void add(long employeeID){
        currentEmployeeID.set(employeeID);
    }

    public static Long get(){
        return currentEmployeeID.get();
    }

    public static void clear(){
        currentEmployeeID.remove();
    }

    @Override
    public Long getEmployeeID() {
        return get();
    }
}

