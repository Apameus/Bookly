package gr.bookapp.storage.file;

import java.util.Map;

public interface ObjectTable<K, V> extends Iterable<Map.Entry<K,V>> {

    void insert(K key, V value);

    V retrieve(K key);

    void delete(K key);

    int size();
}
