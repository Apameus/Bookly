package gr.bookapp.storage.codec;

import gr.bookapp.models.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeCodecTest {
    @TempDir Path dir;
    RandomAccessFile accessFile;
    EmployeeCodec employeeCodec;

    @BeforeEach
    void initialize() throws IOException {
        employeeCodec = new EmployeeCodec(new StringCodec());
        accessFile = new RandomAccessFile(dir.resolve("EmployeeCodec.data").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Serialize-Parse test")
    void serializeParseTest() throws IOException {
        Employee employee = new Employee(1555L, "ilias", "ilias123");
        employeeCodec.write(accessFile, employee);
        accessFile.seek(0);
        assertThat(employeeCodec.read(accessFile)).isEqualTo(employee);
    }

}