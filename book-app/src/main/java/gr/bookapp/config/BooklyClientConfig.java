package gr.bookapp.config;

import java.net.SocketAddress;
import java.nio.file.Path;

public record BooklyClientConfig(Path logsPath, SocketAddress address) {

}
