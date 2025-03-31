package gr.bookapp.common.csv;

import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;

public record UserCsvParser() implements CsvParser<User> {

    @Override
    public User parse(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long id = Long.parseLong(values[0]);
            String username = values[1];
            String password = values[2];
            Role role = Role.valueOf(values[3]);
            return new User(id, username, password, role);
        } catch (Exception e) {
            throw new CsvFileLoadException("Users.csv is incompatible !");
        }
    }
}
