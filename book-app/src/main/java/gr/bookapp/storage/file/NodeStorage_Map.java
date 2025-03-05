package gr.bookapp.storage.file;

import java.util.HashMap;

public interface NodeStorage_Map<K,V> {

    long rootOffset();

    boolean isNull(long nodeOffset) ;

    boolean isFull();

    HashMap<K,V> resize();

    long calculateOffset(K key);

    boolean pointerExceedRange(long pointer) ; //..

    boolean matchKey(long nodeOffset, K key);

    V readValue(long nodeOffset);

    void writeValue(long nodeOffset, V value);

    long readNextOffset(long nodeOffset);

    void writeNode(K key, V value, long offset);

    void deleteNode(long nodeOffset);

    void updateNextOffset(long parentOffset, long childOffset);

    void updateStoredEntries(int by);

    long findEmptySlot(long startingOffset);

}
