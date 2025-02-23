package gr.bookapp.storage;

import gr.bookapp.storage.model.Node;

public interface NodeStorage<K, V> {

    boolean isNull(long nodeOffset);

    void deleteNode(long nodeOffset);

//    void updatePointer(long nodeOffset, long pointer, String leftOrRight);

    Node<K> readNode(long nodeOffset);

    V readValue(long nodeOffset);

    void writeNewNode(K key, V value, long offset);

    void updateNode(K key, V value, long offset);

    void updateStoredEntries(int storedEntries);

    long rootOffset();
}
