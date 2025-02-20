package accessFile;

import accessFile.codec.TreeNode_DualValue_Pointers;
import accessFile.codec.TreeNode_Pointers;

import java.io.IOException;

public interface EntriesAccess<K,V> {

    boolean hasExistFlag(long offset) throws IOException;

    void updateFlag(long offset, byte flag) throws IOException;

    int maxEntrySize();

    int storedEntries();

    void updateStoredEntries(int storedEntries) throws IOException;

    boolean isFull();

    void resize() throws IOException;

    K readKey(long offset) throws IOException; //ToDo: add writeKey ?

    V readValue(long offset) throws IOException; //ToDo: add writeValue ?
    V readValue() throws IOException;

    TreeNode_DualValue_Pointers<K,V> readFullEntry(long offset) throws IOException;
    TreeNode_Pointers<K> readKeyEntry(long offset) throws IOException; //ToDo: add overload method without parameters?
    TreeNode_Pointers<K> readKeyEntry() throws IOException;

    void writeNewEntry(K key, V value, long offset) throws IOException;

    void updateKeyValue(long nodePointer, K newKey, V newValue) throws IOException;

    long updatePointer(long offset, String leftOrRight) throws IOException;
    void updatePointer(long offset, long pointer, String leftOrRight) throws IOException;

    int compare(K a, K b);

    long findEmptySlot() throws IOException;

    TreeNode_DualValue_Pointers<K,V> leftMost(long nodePointer) throws IOException;
}
