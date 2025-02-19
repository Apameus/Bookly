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

    int availableEntries();

    void updateAvailableEntries();

    K readKey(long offset) throws IOException; //ToDo: add writeKey ?

    V readValue(long offset) throws IOException; //ToDo: add writeValue ?

    TreeNode_DualValue_Pointers<K,V> readFullEntry(long offset) throws IOException;
    TreeNode_Pointers<K> readKeyEntry(long offset) throws IOException; //ToDo: add overload class without parameters?

    void writeNewEntry(K key, V value, long offset) throws IOException;

    void updateKeyValue(long nodePointer, K newKey, V newValue) throws IOException;

    long updatePointer(long offset, String leftOrRight) throws IOException;
    void updatePointer(long offset, long pointer, String leftOrRight) throws IOException;

    long findEmptySlot() throws IOException;

    TreeNode_DualValue_Pointers<K,V> leftMost(long nodePointer) throws IOException;
}
