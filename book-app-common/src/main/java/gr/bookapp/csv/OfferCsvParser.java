package gr.bookapp.csv;

import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Offer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record OfferCsvParser() implements CsvParser<Offer> {

    @Override
    public Offer parse(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long offerID = Long.parseLong(values[0]);
            List<String> tags = parseList(values, 1);
            int percentage = Integer.parseInt(values[2 + tags.size()]);
            Instant untilDate = InstantFormatter.parse(values[3 + tags.size()]);
            return new Offer(offerID, tags, percentage, untilDate);
        } catch (Exception e) {
            throw new CsvFileLoadException("Offers.csv is incompatible !");
        }
    }

    private List<String> parseList(String[] inputValues, int index) {
        int listSize = Integer.parseInt(inputValues[index]);
        List<String> list = new ArrayList<>(listSize);
        for (int i = 1; i <= listSize; i++) list.add(inputValues[i + index]);
        return list;
    }

}
