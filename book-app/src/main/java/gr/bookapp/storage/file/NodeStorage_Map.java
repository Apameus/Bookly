package gr.bookapp.storage.file;

public interface NodeStorage_Map<K,V> {

    long rootOffset();

    boolean isNull(long nodeOffset) ;

    boolean pointerExceedRange(long pointer) ;

    boolean matchKey(long nodeOffset, K key);

    V readValue(long nodeOffset);

    void writeNode(K key, V value, long offset);

    void deleteNode(long nodeOffset);

    void updateStoredEntries(int by);

    long findEmptySlot();

}
