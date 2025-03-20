package gr.bookapp.storage.codec;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstantFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy G");

    public static String serialize(Instant instant){
        LocalDate localDate = instant.atZone(ZoneOffset.UTC).toLocalDate();
        return FORMATTER.format(localDate);
    }

    public static Instant parse(String dateString) throws DateTimeParseException {
        LocalDate localDate = LocalDate.parse(dateString, FORMATTER);
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

}
