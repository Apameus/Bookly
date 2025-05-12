package gr.bookapp.repositories;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.User;

import java.util.List;

public interface UserRepository {
    void add(User user) throws InvalidInputException;

    void deleteUserByID(long employeeID);

    User getUserByID(long employeeID);

    User getUserByUsername(String username);

    List<User> getAll();

    boolean adminExist();
}
