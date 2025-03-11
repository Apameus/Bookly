package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Employee;

public final class EmployeeRepository {

    Database<Long, Employee> employeeDatabase;
    Index<Employee, String> usernameIndex = Employee::username;

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
        return employeeDatabase.findAllBy(usernameIndex, username).getFirst();
    }
}
