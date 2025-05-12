package gr.bookapp.protocol.codec;

import gr.bookapp.models.User;
import gr.bookapp.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;

class UserCodecTest {
    @TempDir Path dir;
    RandomAccessFile accessFile;
    UserCodec userCodec;

    @BeforeEach
    void initialize() throws IOException {
        userCodec = new UserCodec(new StringCodec());
        accessFile = new RandomAccessFile(dir.resolve("UserCodec.test").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Serialize-Parse test")
    void serializeParseTest() throws IOException {
        User user = new User(1555L, "ilias", "ilias123", Role.EMPLOYEE);
        userCodec.write(accessFile, user);
        accessFile.seek(0);
        assertThat(userCodec.read(accessFile)).isEqualTo(user);
    }

}