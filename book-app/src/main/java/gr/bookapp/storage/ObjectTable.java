package gr.bookapp.storage;

import java.io.IOException;

public interface ObjectTable<K, V> {

    void insert(K key, V value);

    V retrieve(K key);

    void delete(K key);

}
