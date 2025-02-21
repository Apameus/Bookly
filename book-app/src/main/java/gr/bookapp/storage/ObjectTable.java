package gr.bookapp.storage;

public interface ObjectTable<K, V> {

    void insert(K key, V value);

    V retrieve(K key);

    void delete(K key);

}
