package gr.bookapp.common.csv;

import gr.bookapp.csv.CsvParser;
import gr.bookapp.csv.UserCsvParser;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCsvParserTest {
    CsvParser<User> userCsvParser = new UserCsvParser();

    @Test
    @DisplayName("Parse users from csv")
    void parseUsersFromCsv() throws CsvFileLoadException {
        User userA = new User(11L,"makis","makis123", Role.EMPLOYEE);
        User userB = new User(22L, "admin", "admin123", Role.ADMIN);
        User userC = new User(33L, "fanis", "fanis123", Role.EMPLOYEE);
        String csv = """
                11,makis,makis123,EMPLOYEE
                22,admin,admin123,ADMIN
                33,fanis,fanis123,EMPLOYEE
                """;
        String[] lines = csv.split("\n");
        assertThat(userCsvParser.parse(lines[0])).isEqualTo(userA);
        assertThat(userCsvParser.parse(lines[1])).isEqualTo(userB);
        assertThat(userCsvParser.parse(lines[2])).isEqualTo(userC);
    }

}