package gr.bookapp.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstantFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy G");

    public static String serialize(Instant instant){
        return FORMATTER.format(instant.atZone(ZoneOffset.UTC));
    }

    public static Long serializeLong(Instant instant){
        return instant.getEpochSecond();
    }

    public static Instant parse(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, FORMATTER).atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public static Instant parseLong(Long epochSeconds){
        return Instant.ofEpochSecond(epochSeconds);
    }

}
