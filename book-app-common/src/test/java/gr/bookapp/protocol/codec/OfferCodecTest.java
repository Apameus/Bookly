package gr.bookapp.protocol.codec;

import gr.bookapp.models.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OfferCodecTest {
    @TempDir
    Path dir;
    RandomAccessFile accessFile;
    OfferCodec offerCodec;

    @BeforeEach
    void initialize() throws IOException {
        offerCodec = new OfferCodec(new ListCodec<>(new StringCodec()), new InstantCodec());
        accessFile = new RandomAccessFile(dir.resolve("OfferCodec.test").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Write-Read Offer test")
    void writeReadOfferTest() throws IOException {
        Offer offer = new Offer(9999, List.of("Adventure", "Philosophy"), 15, LocalDate.now().plusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC));
        offerCodec.write(accessFile, offer);
        accessFile.seek(0);
        assertThat(offerCodec.read(accessFile)).isEqualTo(offer);
    }

}