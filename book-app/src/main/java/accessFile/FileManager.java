package accessFile;

import java.io.IOException;

public interface FileManager<K,V> {

    void insert(K key, V value) throws IOException;

    V get(K key) throws IOException;

    void delete(K key) throws IOException;

}
