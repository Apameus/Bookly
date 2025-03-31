package gr.bookapp.log;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class CompositeLogger implements Logger{

    private final Logger[] loggers;
    private final String operatorClassName;

    public CompositeLogger(String operatorClassName, Logger... loggers) {
        this.operatorClassName = operatorClassName;
        this.loggers = loggers;
    }

    @Override
    public void log(String format, Object... args) {
        String date = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        String log = "[%s] %s / %s".formatted(operatorClassName, String.format(format, args), date);

        for (Logger logger : loggers) {
            logger.log(log);
        }
    }

}
