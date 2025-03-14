package gr.bookapp;

public final class AuditContext {
    private static final ThreadLocal<Long> currentEmployee = new ThreadLocal<>();

    public static void addEmployeeID(long employeeID){
        currentEmployee.set(employeeID);
    }

    public static Long getEmployeeID(){
        return currentEmployee.get();
    }

    public static void clear(){
        currentEmployee.remove();
    }
}

