package gr.bookapp.log;

public final class ConsoleLogger implements Logger{

    @Override
    public void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}
