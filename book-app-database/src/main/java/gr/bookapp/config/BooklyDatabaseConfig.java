package gr.bookapp.config;

import java.net.SocketAddress;
import java.nio.file.Path;

public record BooklyDatabaseConfig(Path booksPath, Path bookSalesPath, Path userPath, Path offersPath, Path auditsPath, Path logsPath, SocketAddress address) {

}
