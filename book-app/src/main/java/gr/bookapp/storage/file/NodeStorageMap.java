package gr.bookapp.storage.file;

import java.util.HashMap;

public interface NodeStorageMap<K,V> {

    boolean isNull(long nodeOffset) ;

    boolean isFull();

    HashMap<K,V> resize();

    long calculateOffset(K key);

    boolean matchKey(long nodeOffset, K key);

    K readKey(long nodeOffset);

    V readValue(long nodeOffset);

    void writeValue(long nodeOffset, V value);

    long readNextOffset(long nodeOffset);

    void updateNextOffset(long parentOffset, long childOffset);

    void writeNode(K key, V value, long offset);

    void deleteNode(long nodeOffset);

    long findEmptySlot(long startingOffset);

    void updateStoredEntries(int by);

}
