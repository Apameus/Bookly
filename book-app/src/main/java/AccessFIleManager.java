import java.io.IOException;

public interface AccessFIleManager<K,V> {

    void insert(K key, V value) throws IOException;

    V get(K key) throws IOException;

    void delete(K key) throws IOException;

}
