package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNodeDual;
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
//        if (offset == 0) return;
        if (nodeStorage.isNull(offset)) return;
        Node<K> node = nodeStorage.readNode(offset);
        if (comparator.compare(key, node.key()) < 0) delete(key, node.leftChild());
        else if (comparator.compare(key, node.key()) > 0) delete(key, node.rightChild());
        else {
            if (nodeStorage.isNull(node.leftChild())){
                if (!nodeStorage.isNull(node.rightChild())){
                    var rightChild = readFullEntry(node.rightChild());
                    nodeStorage.updateNode(rightChild.key, rightChild.value, offset);
                }
                else{
                    nodeStorage.deleteNode(offset);
//                    nodeStorage.updatePointer();
                }
//                nodeStorage.updateStoredEntries();
            }
            else if (nodeStorage.isNull(node.rightChild())){
                TreeNodeDual<K, V> leftChild = readFullEntry(node.leftChild());
                nodeStorage.updateNode(leftChild.key, leftChild.value, offset);
                // updateStoredEntries
            }
            else {
                var successor = leftMost(node.rightChild());
                nodeStorage.updateNode(successor.key, successor.value, offset);
                // updateStoredEntries
                delete(successor.key, node.rightChild());
            }
        }
    }

    private TreeNodeDual<K, V> readFullEntry(long offset) {
        Node<K> node = nodeStorage.readNode(offset);
        V value = nodeStorage.readValue(offset);
//        return new TreeNodeDual<K,V>(node, value);
        return new TreeNodeDual<>(node.key(), node.leftChild(), node.rightChild(), value);
    }

    private TreeNodeDual<K,V> leftMost(long offset){
        Node<K> node = nodeStorage.readNode(offset);
        if (nodeStorage.isNull(node.leftChild())) {
            V value = nodeStorage.readValue(offset);
//            return new TreeNodeDual<>(node, value);
            return new TreeNodeDual<>(node.key(), node.leftChild(), node.rightChild(), value);
        }
        return leftMost(node.leftChild());
    }
}
