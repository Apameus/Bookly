package gr.bookapp.log;

public final class CompositeLoggerFactory implements Logger.Factory{
    private final Logger[] loggers;

    public CompositeLoggerFactory(Logger... loggers) {
        this.loggers = loggers;
    }


    @Override
    public Logger create(String operatorClassName) {
        return new CompositeLogger(operatorClassName, loggers);
    }
}
