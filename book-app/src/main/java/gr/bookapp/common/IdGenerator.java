package gr.bookapp.common;

public final class IdGenerator {

    public IdGenerator() {}

    public long generateID(){
        return System.currentTimeMillis();
    }
}
