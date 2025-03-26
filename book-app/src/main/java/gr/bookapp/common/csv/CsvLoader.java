package gr.bookapp.common.csv;

import gr.bookapp.exceptions.ConfigurationFileLoadException;
import gr.bookapp.exceptions.CsvFileLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvLoader {
    public <T>List<T> load(Path csvFile, CsvParser<T> parser) throws CsvFileLoadException {
        List<T> list = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(csvFile);
            for (String line : lines) list.add(parser.parse(line));
        } catch (IOException | CsvFileLoadException e) {throw new CsvFileLoadException("Failed to read config file from specified path !");}
        return list;
    }
}
