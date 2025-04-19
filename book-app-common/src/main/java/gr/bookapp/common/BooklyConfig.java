package gr.bookapp.common; // TODO: Relocate to database module ?

import java.net.SocketAddress;
import java.nio.file.Path;

public record BooklyConfig(Path booksPath, Path bookSalesPath, Path userPath, Path offersPath, Path auditsPath, Path logsPath, SocketAddress address) {

}
