package gr.bookapp.common;

public final class AuditContextImpl implements AuditContext{
    private static final ThreadLocal<Long> CURRENT_EMPLOYEE_ID = new ThreadLocal<>();

    public static void add(long employeeID){
        CURRENT_EMPLOYEE_ID.set(employeeID);
    }

    public static Long get(){
        return CURRENT_EMPLOYEE_ID.get();
    }

    public static void clear(){
        CURRENT_EMPLOYEE_ID.remove();
    }

    @Override
    public Long getEmployeeID() {
        return get();
    }
}

