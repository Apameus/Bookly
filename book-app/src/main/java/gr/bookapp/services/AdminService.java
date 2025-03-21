package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Role;
import gr.bookapp.repositories.EmployeeRepository;

public final class AdminService {
    private final EmployeeRepository employeeRepository;
    private final IdGenerator idGenerator;
    private final Logger logger;


    public AdminService(EmployeeRepository employeeRepository, IdGenerator idGenerator, Logger.Factory loggerFactory) {
        this.employeeRepository = employeeRepository;
        this.idGenerator = idGenerator;
        logger = loggerFactory.create("Admin_Service");
    }

    public void hireEmployee(String username, String password) throws InvalidInputException {
        Employee employee = new Employee(idGenerator.generateID(), username, password, Role.EMPLOYEE);
        employeeRepository.add(employee);
        logger.log("Employee hired");
    }

    public void fireEmployee(long employeeID){
        if (employeeRepository.getEmployeeByID(employeeID) == null) logger.log("Unable to find employee !");
        else {
            employeeRepository.deleteEmployeeByID(employeeID);
            logger.log("Employee fired");
        }
    }

    public Employee searchEmployeeByUsername(String username){
        return employeeRepository.getEmployeeByUsername(username);
    }

}
