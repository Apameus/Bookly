package gr.bookapp.services;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.repositories.UserRepository;
import java.util.List;

public final class UserServiceDbImpl {
    private final UserRepository userRepository;

    public UserServiceDbImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void authenticate(String username, String password) throws AuthenticationFailedException{
        User user = userRepository.getUserByUsername(username);
        if (user == null || !user.password().equals(password)) throw new AuthenticationFailedException();
    }

    public void addUser(String username, String password, Role role) throws InvalidInputException {
        if (userRepository.getUserByUsername(username) != null) throw new InvalidInputException("Username already exist!");
        userRepository.add(new User(username, password, role));
    }

    public void deleteUser(long userID) throws InvalidInputException {
        if (userRepository.getUserByID(userID) == null)
            throw new InvalidInputException("User with specified id does NOT exist!");
        //ToDo: Should we add a check if the user to be deleted is ADMIN ?
        userRepository.deleteUserByID(userID);
    }

    public User getUserByID(long userID) throws InvalidInputException {
        User user = userRepository.getUserByID(userID);
        if (user == null) throw new InvalidInputException("User with specified id does NOT exist!");
        return user;
    }

    public User getUserByUsername(String username) throws InvalidInputException {
        User user = userRepository.getUserByUsername(username);
        if (user == null) throw new InvalidInputException("User with specified username does NOT exist!");
        return user;
    }
    public List<User> getAllUsers() throws InvalidInputException {
        List<User> users = userRepository.getAll();
        if (users.isEmpty()) throw new InvalidInputException("No users registered");
        return users;
    }

    public boolean adminExist() throws InvalidInputException {
        boolean exist = userRepository.adminExist();
        if (!exist) throw new InvalidInputException("Admin user isn't set");
        return exist;
    }
}
