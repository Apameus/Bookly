package gr.bookapp.storage;

import gr.bookapp.storage.model.Node;

public interface NodeStorage<K, V> {

    boolean isNull(long nodeOffset);

    Node<K> readNode(long nodeOffset);

    void writeNewNode(K key, V value, long offset);

    V readValue(long nodeOffset);

    long rootOffset();
}
