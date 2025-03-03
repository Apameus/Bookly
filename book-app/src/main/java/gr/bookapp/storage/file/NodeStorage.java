package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.TreeNode;
import gr.bookapp.storage.codec.TreeNodeDual;

public interface NodeStorage<K, V> {

    long rootOffset();

    boolean isNull(long nodeOffset) ;

    void updatePointer(long parentOffset, String childSide, long childPointer) ;

    V readValue(long nodeOffset) ;

    TreeNode<K> readKeyNode(long nodeOffset) ;

    void writeNode(TreeNodeDual<K,V> node, long offset) ;

    void updateNode(K key, V value, long offset) ;

    void deleteNode(long nodeOffset) ;

    void updateStoredEntries(int by) ;

    long findEmptySlot() ;
}
