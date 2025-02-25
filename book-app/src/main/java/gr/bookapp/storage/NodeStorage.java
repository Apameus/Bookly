package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNode;
import gr.bookapp.storage.codec.TreeNodeDual;

import java.io.IOException;

public interface NodeStorage<K, V> {

    long rootOffset();

    boolean isNull(long nodeOffset) throws IOException;

    void updatePointer(long nodeOffset, long pointer, String leftOrRight) throws IOException;

    V readValue(long nodeOffset) throws IOException;

    TreeNode<K> readKeyNode(long nodeOffset) throws IOException;

    void writeNode(TreeNodeDual<K,V> node, long offset) throws IOException;

    void updateNode(K key, V value, long offset) throws IOException; // **

    void deleteNode(long nodeOffset) throws IOException;

    void updateStoredEntries(int by) throws IOException;

    long findEmptySlot() throws IOException;
}
