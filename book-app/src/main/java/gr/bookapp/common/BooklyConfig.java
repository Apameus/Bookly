package gr.bookapp.common;

import java.nio.file.Path;

public record BooklyConfig(Path booksPath, Path bookSalesPath, Path employeesPath, Path offersPath, Path auditsPath, Path logsPath) {

}
