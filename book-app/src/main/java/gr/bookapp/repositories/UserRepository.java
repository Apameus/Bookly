package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.User;

import java.util.List;

public final class UserRepository {

    private final Database<Long, User> userDatabase;
    private final Index<User, String> usernameIndex = User::username;

    public UserRepository(Database<Long, User> userDatabase) {
        this.userDatabase = userDatabase;
    }

    public void add(User user) throws InvalidInputException {
        if (getUserByUsername(user.username()) != null)
            throw new InvalidInputException("Username already exist !");
        userDatabase.insert(user.id(), user);
    }

    public void deleteEmployeeByID(long employeeID){
        userDatabase.delete(employeeID);
    }

    public User getUserByID(long employeeID){
        return userDatabase.retrieve(employeeID);
    }

    /**
     *
     * @param username of the user
     * @return the user or null if the username doesn't exist
     */
    public User getUserByUsername(String username) {
        return userDatabase.findAllByIndex(usernameIndex, username).stream().findFirst().orElse(null);
    }

    public List<User> getAll() {
        return userDatabase.findAll();
    }
}
