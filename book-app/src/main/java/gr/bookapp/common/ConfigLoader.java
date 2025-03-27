package gr.bookapp.common;

import gr.bookapp.exceptions.ConfigurationFileLoadException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigLoader {
    private final BooklyConfig booklyConfig;
    private final File dataDirectory;

    public ConfigLoader(String pathOfConfigFile) throws ConfigurationFileLoadException {
        String directory = loadDirectory(pathOfConfigFile);
        dataDirectory = new File(directory);

        File books = getFile("Books");
        File bookSales = getFile("BookSales");
        File employees = getFile("Employees");
        File offers = getFile("Offers");
        File audits = getFile("Audits");
        File logs = getFile("Logs");

        booklyConfig = new BooklyConfig(books.toPath(), bookSales.toPath(), employees.toPath(), offers.toPath(), audits.toPath(), logs.toPath());
    }

    private static String loadDirectory(String pathOfConfigFile) throws ConfigurationFileLoadException {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(pathOfConfigFile);
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong from loading the config file from specified path !");
        }

        String directory = properties.getProperty("data.directory");
        if (directory == null || directory.isBlank()) throw new ConfigurationFileLoadException("Missing data.directory from config file !");
        return directory;
    }

    private File getFile(String fileName) throws ConfigurationFileLoadException {
        File file = new File(dataDirectory, fileName);
        try {
            if (!file.exists() && !file.createNewFile()) throw new ConfigurationFileLoadException("Failed create file: " + fileName);
        } catch (IOException e) {
            throw new ConfigurationFileLoadException("Error creating the file: " + fileName);
        }
        return file;
    }

    public BooklyConfig get() { return booklyConfig; }


}
