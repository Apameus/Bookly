package gr.bookapp.services;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.User;
import gr.bookapp.models.Role;
import gr.bookapp.repositories.UserRepository;

public final class AdminService {
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final IdGenerator idGenerator;
    private final Logger logger;


    public AdminService(UserRepository userRepository, AuditService auditService, IdGenerator idGenerator, Logger.Factory loggerFactory) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.idGenerator = idGenerator;
        logger = loggerFactory.create("Admin_Service");
    }

    public void hireEmployee(String username, String password) throws InvalidInputException {
        User user = new User(idGenerator.generateID(), username, password, Role.EMPLOYEE);
        userRepository.add(user);
        auditService.audit("Employee hired");
        logger.log("Employee hired from admin with id: " + AuditContextImpl.get());
    }

    public void fireEmployee(long employeeID){
        if (userRepository.getUserByID(employeeID) == null) logger.log("Unable to find employee !");
        else {
            userRepository.deleteEmployeeByID(employeeID);
            auditService.audit("Employee fired");
            logger.log("Employee fired from admin with id: " + AuditContextImpl.get());
        }
    }

    public User searchEmployeeByUsername(String username){
        return userRepository.getUserByUsername(username);
    }

}
