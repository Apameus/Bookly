package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNode;

public interface NodeStorage<K, V> {

    long rootOffset();

    boolean isNull(long nodeOffset);

    void updatePointer(long nodeOffset, long pointer, String leftOrRight);

    TreeNode<K> readNode(long nodeOffset);

    V readValue(long nodeOffset);

    void writeNewNode(K key, V value, long offset);

    void updateNode(K key, V value, long offset);

    void deleteNode(long nodeOffset);

    void updateStoredEntries(int by);
}
