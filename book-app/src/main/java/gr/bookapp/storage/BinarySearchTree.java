package gr.bookapp.storage;

import gr.bookapp.storage.model.Node;

import java.util.Comparator;

public final class BinarySearchTree<K, V> implements ObjectTable<K, V> {

    private final Comparator<K> comparator;
    private final NodeStorage<K, V> nodeStorage;

    public BinarySearchTree(Comparator<K> comparator, NodeStorage<K, V> nodeStorage) {
        this.comparator = comparator;
        this.nodeStorage = nodeStorage;
    }

    @Override
    public void insert(K key, V value) {
        insertAtOffset(key, value, nodeStorage.rootOffset());
    }
    private void insertAtOffset(K key, V value, long offset) {
//        if (offset == 0) return;
        if (nodeStorage.isNull(offset)){
            nodeStorage.writeNewNode(key, value, offset);
            // updateStoredEntries
            return;
        }
        Node<K> found = nodeStorage.readNode(offset);
        if (comparator.compare(key, found.key()) < 0) insertAtOffset(key, value, found.leftChild());
        else if (comparator.compare(key, found.key()) > 0) insertAtOffset(key, value, found.rightChild());
    }

    @Override
    public V retrieve(K key) {
        return retrieveFromOffset(key, nodeStorage.rootOffset());
    }
    private V retrieveFromOffset(K key, long offset){
        if (nodeStorage.isNull(offset)){
            return null;
        }
        Node<K> node = nodeStorage.readNode(offset);
        int compare = comparator.compare(key, node.key());
        if (compare < 0){
            // key < node.key
            return retrieveFromOffset(key, node.leftChild());
        }
        if (compare > 0) {
            // key > node.key
            return retrieveFromOffset(key, node.rightChild());
        }
        // key == node.key
        return nodeStorage.readValue(offset);
    }

    @Override
    public void delete(K key) {
        delete(key, nodeStorage.rootOffset());
    }
    private void delete(K key, long offset) {
        if (offset == 0) return;

    }
}
