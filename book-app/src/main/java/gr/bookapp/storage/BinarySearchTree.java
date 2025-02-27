package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNodeDual;
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
        insert(key, value, nodeStorage.rootOffset());}
    private void insert(K key, V value, long offset) {
        if (nodeStorage.isNull(offset)){ // If the offset is empty we insert the node there
            writeNewNode(new TreeNodeDual<>(key, value), offset, +1);
            return;
        }
        var found = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, found.key());
        if (compare < 0) insertNewNode(found.leftPointer(), key, value, offset, "L");
        else if (compare > 0) insertNewNode(found.rightPointer(), key, value, offset, "R");
        else nodeStorage.updateNode(key, value, offset);
    }

    private void insertNewNode(long childPointer, K key, V value, long parentOffset, String childSide) {
        if (nodeStorage.isNull(childPointer)) {
            insertNewNode(key, value, parentOffset, childSide);
            return;
        }
        insert(key, value, childPointer);
    }

    private void insertNewNode(K key, V value, long parentOffset, String childSide) {
        long emptySlot = nodeStorage.findEmptySlot();
        nodeStorage.updatePointer(parentOffset, childSide, emptySlot);
        writeNewNode(new TreeNodeDual<>(key, value), emptySlot, +1);
    }

    @Override
    public V retrieve(K key) {return retrieveFromOffset(key, nodeStorage.rootOffset());}
    private V retrieveFromOffset(K key, long offset) {
        if (nodeStorage.isNull(offset)) return null;
        var node = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, node.key());
        if (compare < 0) return retrieveFromOffset(key, node.leftPointer());
        if (compare > 0) return retrieveFromOffset(key, node.rightPointer());
        return nodeStorage.readValue(offset);
    }

    @Override
    public void delete(K key) {delete(key, nodeStorage.rootOffset(), 0, "");}
    private void delete(K key, long offset, long parentOffset, String childSide) {
        if (nodeStorage.isNull(offset)) return;
        var node = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, node.key());
        if (compare < 0) delete(key, node.leftPointer(), offset, "L");
        else if (compare > 0) delete(key, node.rightPointer(), offset, "R");
        else {
            if (nodeStorage.isNull(node.leftPointer())){ // Left child is null
                if (!nodeStorage.isNull(node.rightPointer())){
                    var rightChild = readFullEntry(node.rightPointer());
                    nodeStorage.writeNode(rightChild, offset);
                }
                else{ // Both children are null
                    nodeStorage.deleteNode(offset);
                    nodeStorage.updatePointer(parentOffset, childSide, 0);
                }
                nodeStorage.updateStoredEntries(-1);
            }
            else if (nodeStorage.isNull(node.rightPointer())){ // Right child is null
                var leftChild = readFullEntry(node.leftPointer());
                writeNewNode(leftChild, offset, -1);
            }
            else { // both children are present
                var successor = leftMost(node.rightPointer());
                nodeStorage.updateNode(successor.key(), successor.value(), offset);
                nodeStorage.updateStoredEntries(-1);
                delete(successor.key(), node.rightPointer(), offset, "R");
            }
        }
    }

    private TreeNodeDual<K, V> readFullEntry(long offset) {
        var node = nodeStorage.readKeyNode(offset);
        V value = nodeStorage.readValue(offset);
        return new TreeNodeDual<K,V>(node, value);
    }

    private TreeNodeDual<K,V> leftMost(long offset) {
        var node = nodeStorage.readKeyNode(offset);
        if (nodeStorage.isNull(node.leftPointer())) {
            V value = nodeStorage.readValue(offset);
            return new TreeNodeDual<>(node, value);
        }
        return leftMost(node.leftPointer());
    }

    private void writeNewNode(TreeNodeDual<K, V> node, long offset, int by) {
        nodeStorage.writeNode(node, offset);
        nodeStorage.updateStoredEntries(by);
    }
}
