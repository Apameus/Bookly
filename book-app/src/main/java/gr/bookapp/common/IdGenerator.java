package gr.bookapp.common;

public final class IdGenerator {
//    private final long epoch = 1735689600000L; // JAN 1 2025

    public IdGenerator() {}

    public long generateID(){
//        long timeStamp = epoch - System.currentTimeMillis();
//        return timeStamp;
        return System.currentTimeMillis();
    }
}
