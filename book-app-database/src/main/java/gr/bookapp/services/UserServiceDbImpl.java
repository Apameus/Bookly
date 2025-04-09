package gr.bookapp.services;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.repositories.UserRepositoryDbImpl;

import java.util.List;

public final class UserServiceDbImpl {
    private final UserRepositoryDbImpl userRepositoryDb;
    private final IdGenerator idGenerator;

    public UserServiceDbImpl(UserRepositoryDbImpl userRepositoryDb, IdGenerator idGenerator) {
        this.userRepositoryDb = userRepositoryDb;
        this.idGenerator = idGenerator;
    }

    public void authenticate(String username, String password) throws AuthenticationFailedException{
        User user = userRepositoryDb.getUserByUsername(username);
        if (user == null || !user.password().equals(password)) throw new AuthenticationFailedException();
    }

    public void addUser(String username, String password, Role role) throws InvalidInputException {
        if (userRepositoryDb.getUserByUsername(username) != null) throw new InvalidInputException("Username already exist!");
        userRepositoryDb.add(
                new User(idGenerator.generateID(), username, password, role));
    }

    public void deleteUser(long userID) throws InvalidInputException {
        if (userRepositoryDb.getUserByID(userID) == null)
            throw new InvalidInputException("User with id: %s does NOT exist!".formatted(userID));
        //ToDo: Should we add a check if the user to be deleted is ADMIN ?
        userRepositoryDb.deleteUserByID(userID);
    }

    public User getUserByID(long userID) throws InvalidInputException {
        User user = userRepositoryDb.getUserByID(userID);
        if (user == null) throw new InvalidInputException("User with specified id does NOT exist!");
        return user;
    }

    public User getUserByUsername(String username) throws InvalidInputException {
        User user = userRepositoryDb.getUserByUsername(username);
        if (user == null) throw new InvalidInputException("User with specified username does NOT exist!");
        return user;
    }
    public List<User> getAllUsers() throws InvalidInputException {
        List<User> users = userRepositoryDb.getAll();
        if (users.isEmpty()) throw new InvalidInputException("No users registered");
        return users;
    }
}
