package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Employee;

public final class EmployeeRepository {

    private final Database<Long, Employee> employeeDatabase;
    private final Index<Employee, String> usernameIndex = Employee::username;

    public EmployeeRepository(Database<Long, Employee> employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void add(Employee employee){
        employeeDatabase.insert(employee.id(), employee);
    }

    public void deleteEmployeeByID(long employeeID){
        employeeDatabase.delete(employeeID);
    }

    public Employee getEmployeeByID(long employeeID){
        return employeeDatabase.retrieve(employeeID);
    }

    public Employee getEmployeeByUsername(String username){
        return employeeDatabase.findAllByIndex(usernameIndex, username).getFirst();
    }
}
