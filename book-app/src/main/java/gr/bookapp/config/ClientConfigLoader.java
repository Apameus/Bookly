package gr.bookapp.config;

import gr.bookapp.exceptions.ConfigurationFileLoadException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ClientConfigLoader {
    private final BooklyClientConfig booklyClientConfig;
    private final String LOGS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app/src/main/resources/Logs";

    public ClientConfigLoader() throws ConfigurationFileLoadException, IOException {
        //
        String configFilePath = System.getenv("BOOKLY_CLIENT_CONFIG_PATH");
        if (configFilePath == null) {
            throw new IllegalStateException("BOOKLY_CLIENT_CONFIG_PATH env var is not set!");
        }
        Properties config = new Properties();
        try(var reader = Files.newBufferedReader(Path.of(configFilePath))) {
            config.load(reader);
        }


        Path logs = Path.of((String) config.getOrDefault("logs.path", LOGS_DEFAULT_FILE_PATH));
        SocketAddress address = new InetSocketAddress(config.getProperty("socket.address"), Integer.parseInt(config.getProperty("socket.port")));

        ensureExistenceOfFiles(logs);

        booklyClientConfig = new BooklyClientConfig(logs, address);
    }

    private static void ensureExistenceOfFiles(Path... files) throws ConfigurationFileLoadException {
        for (Path file : files) {
            if (Files.notExists(file)) {
                try {
                    Files.createFile(file);
                } catch (IOException e) {
                    throw new ConfigurationFileLoadException("Failed to create file: %s".formatted(file.getFileName().toString()));
                }
            }
        }
    }


    public BooklyClientConfig get() { return booklyClientConfig; }


}
