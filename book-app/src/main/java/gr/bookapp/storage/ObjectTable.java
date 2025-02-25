package gr.bookapp.storage;

import java.io.IOException;

public interface ObjectTable<K, V> {

    void insert(K key, V value) throws IOException;

    V retrieve(K key) throws IOException;

    void delete(K key) throws IOException;

}
