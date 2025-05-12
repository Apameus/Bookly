package gr.bookapp.config;

import gr.bookapp.exceptions.ConfigurationFileLoadException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfigLoader { //TODO: Refactor from Path -> Properties
    private final BooklyDatabaseConfig booklyDatabaseConfig;
    private final String BOOKS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/Books";
    private final String BOOKSALES_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/BookSales";
    private final String USERS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/Users";
    private final String OFFERS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/Offers";
    private final String AUDITS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/Audits";
    private final String LOGS_DEFAULT_FILE_PATH = "/home/zeus/Dev/MainProjects/bookly/book-app-database/src/main/resources/Logs";

    public DatabaseConfigLoader() throws ConfigurationFileLoadException, IOException {
        //
        String configFilePath = System.getenv("BOOKLY_DATABASE_CONFIG_PATH");
        if (configFilePath == null) {
            throw new IllegalStateException("BOOKLY_DATABASE_CONFIG_PATH env var is not set!");
        }
        Properties config = new Properties();
        try(var reader = Files.newBufferedReader(Path.of(configFilePath))) {
            config.load(reader);
        }
        //

//        Path dataDirectory = loadDataDirectory(pathOfConfigFile);

//        Path books = dataDirectory.resolve("Books");
//        Path bookSales = dataDirectory.resolve("BookSales");
//        Path users = dataDirectory.resolve("Users");
//        Path offers = dataDirectory.resolve("Offers");
//        Path audits = dataDirectory.resolve("Audits");
//        Path logs = dataDirectory.resolve("Logs");
//        SocketAddress address = loadAddress(dataDirectory.resolve("Address"));

        Path books = Path.of((String) config.getOrDefault("books.path", BOOKS_DEFAULT_FILE_PATH));
        Path bookSales = Path.of((String) config.getOrDefault("bookSales.path", BOOKSALES_DEFAULT_FILE_PATH));
        Path users = Path.of((String) config.getOrDefault("users.path", USERS_DEFAULT_FILE_PATH));
        Path offers = Path.of((String) config.getOrDefault("offers.path", OFFERS_DEFAULT_FILE_PATH));
        Path audits = Path.of((String) config.getOrDefault("audits.path", AUDITS_DEFAULT_FILE_PATH));
        Path logs = Path.of((String) config.getOrDefault("logs.path", LOGS_DEFAULT_FILE_PATH));
        SocketAddress address = new InetSocketAddress(config.getProperty("socket.address"), Integer.parseInt(config.getProperty("socket.port")));

        ensureExistenceOfFiles(books, bookSales, users, offers, audits, logs);

        booklyDatabaseConfig = new BooklyDatabaseConfig(books, bookSales, users, offers, audits, logs, address);
    }

//    private static Path loadDataDirectory(String pathOfConfigFile) throws ConfigurationFileLoadException {
//        Path configPath = Path.of(pathOfConfigFile);
//        if (Files.notExists(configPath)) throw new ConfigurationFileLoadException("Failed to find config file from specified path");
//        Properties properties = new Properties();
//        try(BufferedReader br = Files.newBufferedReader(configPath)) {
//            properties.load(br);
//        } catch (IOException e) {
//            throw new ConfigurationFileLoadException("Failed to load the directory path from config file !");
//        }
//        String dataDirPath = properties.getProperty("data.directory");
//        return Paths.get(dataDirPath);
//    }

//    private SocketAddress loadAddress(Path dataDirectory) throws IOException {
//        String[] parts = Files.readString(dataDirectory).trim().split(":");
//        if (parts.length != 2) throw new IllegalStateException("Invalid Socket Address format. Expected 'host:port'");
//        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
//    }

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


    public BooklyDatabaseConfig get() { return booklyDatabaseConfig; }


}
