package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNodeDual;
import gr.bookapp.storage.codec.TreeNode;

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
        if (nodeStorage.isNull(offset)){
            nodeStorage.writeNewNode(key, value, offset);
             nodeStorage.updateStoredEntries(+1);
            return;
        }
        TreeNode<K> found = nodeStorage.readNode(offset);
        int compare = comparator.compare(key, found.key);
        if (compare < 0) insertAtOffset(key, value, found.leftPointer);
        else if (compare > 0) insertAtOffset(key, value, found.rightPointer);
    }

    @Override
    public V retrieve(K key) {
        return retrieveFromOffset(key, nodeStorage.rootOffset());
    }
    private V retrieveFromOffset(K key, long offset){
        if (nodeStorage.isNull(offset)) return null;
        TreeNode<K> node = nodeStorage.readNode(offset);
        int compare = comparator.compare(key, node.key);
        if (compare < 0) return retrieveFromOffset(key, node.leftPointer);
        if (compare > 0) return retrieveFromOffset(key, node.rightPointer);
        return nodeStorage.readValue(offset);
    }

    @Override
    public void delete(K key) {
        delete(key, nodeStorage.rootOffset(), 0, "");
    }
    private void delete(K key, long offset, long prevOffset, String prevLeftOrRight) {
        if (nodeStorage.isNull(offset)) return;
        TreeNode<K> node = nodeStorage.readNode(offset);
        int compare = comparator.compare(key, node.key);
        if (compare < 0) delete(key, node.leftPointer, offset, "L");
        else if (compare > 0) delete(key, node.rightPointer, offset, "R");
        else {
            if (nodeStorage.isNull(node.leftPointer)){
                if (!nodeStorage.isNull(node.rightPointer)){
                    var rightChild = readFullEntry(node.rightPointer);
                    nodeStorage.updateNode(rightChild.key, rightChild.value, offset);
                }
                else{
                    nodeStorage.deleteNode(offset);
                    nodeStorage.updatePointer(prevOffset, 0, prevLeftOrRight);
                }
                nodeStorage.updateStoredEntries(-1);
            }
            else if (nodeStorage.isNull(node.rightPointer)){
                TreeNodeDual<K, V> leftChild = readFullEntry(node.leftPointer);
                nodeStorage.updateNode(leftChild.key, leftChild.value, offset);
                 nodeStorage.updateStoredEntries(-1);
            }
            else {
                var successor = leftMost(node.rightPointer);
                nodeStorage.updateNode(successor.key, successor.value, offset);
                nodeStorage.updateStoredEntries(-1);
                delete(successor.key, node.rightPointer, offset, "R");
            }
        }
    }

    private TreeNodeDual<K, V> readFullEntry(long offset) {
        TreeNode<K> node = nodeStorage.readNode(offset);
        V value = nodeStorage.readValue(offset);
        return new TreeNodeDual<K,V>(node, value);
    }

    private TreeNodeDual<K,V> leftMost(long offset){
        TreeNode<K> node = nodeStorage.readNode(offset);
        if (nodeStorage.isNull(node.leftPointer)) {
            V value = nodeStorage.readValue(offset);
            return new TreeNodeDual<>(node, value);
        }
        return leftMost(node.leftPointer);
    }
}
