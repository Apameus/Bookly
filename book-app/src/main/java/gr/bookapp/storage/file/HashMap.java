package gr.bookapp.storage.file;

import java.util.Comparator;

public final class HashMap<K,V> implements ObjectTable<K,V>{

    Comparator<K> comparator;
    NodeStorage_Map<K,V> nodeStorage;

    public HashMap(Comparator<K> comparator, NodeStorage_Map<K,V> nodeStorage) {
        this.comparator = comparator;
        this.nodeStorage = nodeStorage;
    }

    @Override
    public void insert(K key, V value) {

    }

    @Override
    public V retrieve(K key) {
        return null;
    }

    @Override
    public void delete(K key) {

    }
}
