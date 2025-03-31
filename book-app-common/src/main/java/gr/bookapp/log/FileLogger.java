package gr.bookapp.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public final class FileLogger implements Logger{
    private final Path path;
    private final BufferedWriter writer;

    public FileLogger(Path path) {
        this.path = path;
        try {
            writer = Files.newBufferedWriter(path, CREATE, APPEND, WRITE);
        }catch (IOException e) { throw new RuntimeException(); }
    }

    @Override
    public void log(String format, Object... args) {
        try {
            writer.append(String.format(format,args));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
