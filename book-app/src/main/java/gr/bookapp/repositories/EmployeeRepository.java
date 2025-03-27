package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Employee;

public final class EmployeeRepository {

    private final Database<Long, Employee> employeeDatabase;
    private final Index<Employee, String> usernameIndex = Employee::username;

    public EmployeeRepository(Database<Long, Employee> employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public void add(Employee employee) throws InvalidInputException {
        if (getEmployeeByUsername(employee.username()) != null)
            throw new InvalidInputException("Username already exist !");
        employeeDatabase.insert(employee.id(), employee);
    }

    public void deleteEmployeeByID(long employeeID){
        employeeDatabase.delete(employeeID);
    }

    public Employee getEmployeeByID(long employeeID){
        return employeeDatabase.retrieve(employeeID);
    }

    public Employee getEmployeeByUsername(String username) {
        return employeeDatabase.findAllByIndex(usernameIndex, username).stream().findFirst().orElse(null);
    }

}
