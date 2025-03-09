package gr.bookapp.storage.file;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public final class HashMap<K,V> implements ObjectTable<K,V>{

    Comparator<K> comparator;
    NodeStorageMap<K,V> nodeStorage;

    public HashMap(Comparator<K> comparator, NodeStorageMap<K,V> nodeStorage) {
        this.comparator = comparator;
        this.nodeStorage = nodeStorage;
    }

    @Override
    public void insert(K key, V value) {
        insert(key, value, nodeStorage.calculateOffset(key));
    }
    private void insert(K key, V value, long offset){
        if (nodeStorage.isFull()) resize();
        if (nodeStorage.isNull(offset)) nodeStorage.writeNode(key, value, offset);
        else {
            if (nodeStorage.matchKey(offset, key)) {
                nodeStorage.writeValue(offset, value);
                return;
            }
            long nextOffset = nodeStorage.readNextOffset(offset);
            if (nodeStorage.isNull(nextOffset)){
                nextOffset = nodeStorage.findEmptySlot(nextOffset);
                nodeStorage.updateNextOffset(offset, nextOffset);
            }
            insert(key, value, nextOffset);
            return;
        }
        nodeStorage.updateStoredEntries(+1);
    }

     @Override
    public V retrieve(K key) {
        long offset = nodeStorage.calculateOffset(key);
        return retrieve(key, offset);
    }
    private V retrieve(K key, long offset){
        if (nodeStorage.isNull(offset)) return null;
        else if (nodeStorage.matchKey(offset, key)) return nodeStorage.readValue(offset);
        else return retrieve(key, nodeStorage.readNextOffset(offset));
    }

    @Override
    public void delete(K key) {
        delete(key, nodeStorage.calculateOffset(key));
    }
    private void delete(K key, long offset){
        if (nodeStorage.isNull(offset)) return;
        long nextOffset = nodeStorage.readNextOffset(offset);

        if (nodeStorage.matchKey(offset, key)) {
            if (nodeStorage.isNull(nextOffset)) nodeStorage.deleteNode(offset);
            else {
                nodeStorage.writeNode(nodeStorage.readKey(nextOffset), nodeStorage.readValue(nextOffset), offset);
                nodeStorage.updateNextOffset(offset, nodeStorage.readNextOffset(nextOffset));
            }
            nodeStorage.updateStoredEntries(-1);
        }
        else delete(key, nextOffset);
    }

    private void resize() {
        java.util.HashMap<K, V> prevMap = nodeStorage.resize();
        prevMap.forEach(this::insert);
    }


    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return nodeStorage.entriesIterator().entrySet().iterator();
    }
}
