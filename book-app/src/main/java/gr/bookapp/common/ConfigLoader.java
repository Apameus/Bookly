package gr.bookapp.common;

import gr.bookapp.exceptions.ConfigurationFileLoadException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigLoader {
    private final BooklyConfig booklyConfig;

    public ConfigLoader(String pathOfConfigFile) throws ConfigurationFileLoadException {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(pathOfConfigFile);
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong from loading the config file from specified path !");
        }

        String books = properties.getProperty("booksFile.path");
        String bookSales = properties.getProperty("bookSalesFile.path");
        String employees = properties.getProperty("employeesFile.path");
        String offers = properties.getProperty("offersFile.path");
        String audits = properties.getProperty("auditsFile.path");
        String logs = properties.getProperty("logsFile.path");
        if (books == null) throw new ConfigurationFileLoadException("Missing \"booksFile.path\" from config file !");
        if (bookSales == null) throw new ConfigurationFileLoadException("Missing \"bookSales.path\" from config file !");
        if (employees == null) throw new ConfigurationFileLoadException("Missing \"employeesFile.path\" from config file !");
        if (offers == null) throw new ConfigurationFileLoadException("Missing \"offersFile.path\" from config file !");
        if (audits == null) throw new ConfigurationFileLoadException("Missing \"auditsFile.path\" from config file !");
        if (logs == null) throw new ConfigurationFileLoadException("Missing \"logsFile.path\" from config file !");

        booklyConfig = new BooklyConfig(Path.of(books), Path.of(bookSales), Path.of(employees), Path.of(offers), Path.of(audits), Path.of(logs));
    }

    public BooklyConfig get() { return booklyConfig; }


}
