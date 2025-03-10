package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.Employee;

public final class EmployeeRepository {

    Database<Long, Employee> employeeDatabase;

    public void add(Employee employee){
        employeeDatabase.insert(employee.id(), employee);
    }

    public void delete(long employeeID){
        employeeDatabase.delete(employeeID);
    }

    public Employee get(long employeeID){
        return employeeDatabase.retrieve(employeeID);
    }
}
