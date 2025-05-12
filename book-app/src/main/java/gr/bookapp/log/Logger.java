package gr.bookapp.log;

public interface Logger {

    void log(String format, Object... args);

    interface Factory {
        Logger create(String operatorClassName);
    }

}
