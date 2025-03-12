package gr.bookapp.storage.file;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface NodeStorageMap<K,V> {

    boolean isNull(long nodeOffset) ;

    boolean isFull();

    HashMap<K,V> resize();

    int size();

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

    Iterator<Map.Entry<K,V>> entriesIterator();
}
