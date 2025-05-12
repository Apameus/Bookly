package gr.bookapp.storage.codec;

import gr.bookapp.protocol.codec.InstantCodec;
import gr.bookapp.protocol.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class AuditStreamFileCodecTest {
    @TempDir
    Path dir;
    RandomAccessFile accessFile;
    AuditCodec auditCodec;

    @BeforeEach
    void initialize() throws IOException {
        StringCodec stringCodec = new StringCodec(100);
        auditCodec = new AuditCodec(stringCodec, new InstantCodec());
        accessFile = new RandomAccessFile(dir.resolve("EmployeeCodec.data").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Write-Read Audit Test")
    void writeReadAuditTest() throws IOException {
        Audit audit = new Audit(7, "Employee with id 7 sold a book with id 324623", LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant());
        auditCodec.write(accessFile, audit);
        accessFile.seek(0);
        assertThat(auditCodec.read(accessFile)).isEqualTo(audit);
    }

}