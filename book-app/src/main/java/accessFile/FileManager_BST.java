package accessFile;

import java.io.IOException;

public final class FileManager_BST<K,V> implements FileManager<K,V> {
    private final EntriesAccess<K,V> access;
    private final byte EXIST_FLAG = 1;
    private final byte NULL_FLAG = 0;
    private int storedEntries;

    public FileManager_BST(EntriesAccess<K,V> access) {
        this.access = access;
        storedEntries = access.storedEntries();
    }

    @Override
    public void insert(K key, V value) throws IOException {
        insert(key, value, 4);
    }
    private void insert(K key, V value, long offset) throws IOException {
        if (offset == 0) return;
        if (access.isFull()) access.resize();
        while (access.hasExistFlag(offset)) {
            var found = access.readKeyEntry();
            if (access.compare(key, found.data) < 0){
                if (found.leftPointer == 0){
                    long leftPointer = access.updatePointer(offset, "L");
                    insert(key, value, leftPointer);
                    return;
                }
                offset = found.leftPointer;
            }
            else if (access.compare(key, found.data) > 0){
                if (found.rightPointer == 0){
                    long rightPointer = access.updatePointer(offset, "R");
                    insert(key,value, found.rightPointer);
                    return;
                }
                offset = found.rightPointer;
            }
        }
        access.writeNewEntry(key, value, offset);
        access.updateStoredEntries(++storedEntries);
    }

    @Override
    public V get(K key) throws IOException {
        return get(key, 4);
    }
    private V get(K key, long offset) throws IOException {
        if (offset == 0) return null;
        var node = access.readKeyEntry(offset);
        if (access.compare(key, node.data) < 0) return get(key, node.leftPointer);
        else if (access.compare(key, node.data) > 0) return get(key, node.rightPointer);
        else return access.readValue();
    }

    @Override
    public void delete(K key) throws IOException {
        delete(key, 4, 0, "");
    }
    private void delete(K key, long offset, long prevOffset, String prevLeftOrRight) throws IOException {
        if (offset == 0) return;
        var node = access.readKeyEntry(offset);
        if (access.compare(key, node.data) < 0) delete(key, node.leftPointer, offset, "L");
        else if (access.compare(key, node.data) > 0) delete(key, node.rightPointer, offset, "R");
        else {
            if (node.leftPointer == 0){
                if (node.rightPointer != 0){
                    var rightChild = access.readFullEntry(node.rightPointer);
                    access.updateKeyValue(offset, rightChild.key, rightChild.value);
                }
                else {
                    access.updateFlag(offset, NULL_FLAG);
                    access.updatePointer(prevOffset, 0, prevLeftOrRight);
                }
                access.updateStoredEntries(--storedEntries);
            }
            else if (node.rightPointer == 0) {
                var leftChild = access.readFullEntry(node.leftPointer);
                access.updateKeyValue(offset, leftChild.key, leftChild.value);
                access.updateStoredEntries(--storedEntries);
            }
            else {
                var successor = access.leftMost(node.rightPointer);
                access.updateKeyValue(offset, successor.key, successor.value);
                access.updateStoredEntries(storedEntries - 1);
                delete(successor.key, node.rightPointer, offset, "R");
            }
        }
    }

}
