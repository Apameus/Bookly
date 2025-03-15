package gr.bookapp.common;

public final class AuditContextImp implements AuditContext{
    private static final ThreadLocal<Long> currentEmployee = new ThreadLocal<>();

    public static void add(long employeeID){
        currentEmployee.set(employeeID);
    }

    public static Long get(){
        return currentEmployee.get();
    }

    public static void clear(){
        currentEmployee.remove();
    }

    @Override
    public Long getEmployeeID() {
        return get();
    }
}

