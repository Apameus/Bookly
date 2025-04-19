package gr.bookapp.common;

import gr.bookapp.exceptions.ConfigurationFileLoadException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigLoader { //TODO: Refactor from Path -> Properties
    private final BooklyConfig booklyConfig;

    public ConfigLoader(String pathOfConfigFile) throws ConfigurationFileLoadException, IOException {

        Path dataDirectory = loadDataDirectory(pathOfConfigFile);

        Path books = dataDirectory.resolve("Books");
        Path bookSales = dataDirectory.resolve("BookSales");
        Path users = dataDirectory.resolve("Users");
        Path offers = dataDirectory.resolve("Offers");
        Path audits = dataDirectory.resolve("Audits");
        Path logs = dataDirectory.resolve("Logs");
        SocketAddress address = loadAddress(dataDirectory.resolve("Address"));
        ensureExistenceOfFiles(books, bookSales, users, offers, audits, logs);

        booklyConfig = new BooklyConfig(books, bookSales, users, offers, audits, logs, address);
    }

    private SocketAddress loadAddress(Path dataDirectory) throws IOException {
        String[] parts = Files.readString(dataDirectory).trim().split(":");
        if (parts.length != 2) throw new IllegalStateException("Invalid Socket Address format. Expected 'host:port'");
        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
    }

    private static Path loadDataDirectory(String pathOfConfigFile) throws ConfigurationFileLoadException {
        Path configPath = Path.of(pathOfConfigFile);
        if (Files.notExists(configPath)) throw new ConfigurationFileLoadException("Failed to find config file from specified path");
        Properties properties = new Properties();
        try(BufferedReader br = Files.newBufferedReader(configPath)) {
            properties.load(br);
        } catch (IOException e) {
            throw new ConfigurationFileLoadException("Failed to load the directory path from config file !");
        }
        String dataDirPath = properties.getProperty("data.directory");
        return Paths.get(dataDirPath);
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


    public BooklyConfig get() { return booklyConfig; }


}
