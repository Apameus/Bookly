package gr.bookapp.storage;

import gr.bookapp.storage.codec.TreeNodeDual;
import gr.bookapp.storage.codec.TreeNode;

import java.io.IOException;
import java.util.Comparator;

public final class BinarySearchTree<K, V> implements ObjectTable<K, V> {

    private final Comparator<K> comparator;
    private final NodeStorage<K, V> nodeStorage;

    public BinarySearchTree(Comparator<K> comparator, NodeStorage<K, V> nodeStorage) {
        this.comparator = comparator;
        this.nodeStorage = nodeStorage;
    }

    @Override
    public void insert(K key, V value) throws IOException {insert(key, value, nodeStorage.rootOffset(), nodeStorage.rootOffset());}
    private void insert(K key, V value, long offset, long prevOffset) throws IOException {
        if (nodeStorage. isNull(offset)){
            writeNewNode(new TreeNodeDual<>(key, value), offset, +1);
            return;
        }
        var found = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, found.key());
        if (compare < 0) {
            if (nodeStorage.isNull(found.leftPointer())) {
                long emptySlot = nodeStorage.findEmptySlot();
                nodeStorage.updatePointer(prevOffset, emptySlot, "L");
                writeNewNode(new TreeNodeDual<>(key, value), emptySlot, +1);
                return;
            }
            insert(key, value, found.leftPointer(), offset);
        }
        else if (compare > 0) {
            if (nodeStorage.isNull(found.rightPointer())){
                long emptySlot = nodeStorage.findEmptySlot();
                nodeStorage.updatePointer(prevOffset, emptySlot, "R");
                writeNewNode(new TreeNodeDual<>(key, value), emptySlot, +1);
                return;
            }
            insert(key, value, found.rightPointer(), offset);
        }
        else nodeStorage.updateNode(key, value, offset);
    }

    private void writeNewNode(TreeNodeDual<K, V> node, long offset, int by) throws IOException {
        nodeStorage.writeNode(node, offset);
        nodeStorage.updateStoredEntries(by);
    }

    @Override
    public V retrieve(K key) throws IOException {return retrieveFromOffset(key, nodeStorage.rootOffset());}
    private V retrieveFromOffset(K key, long offset) throws IOException {
        if (nodeStorage.isNull(offset)) return null;
        var node = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, node.key());
        if (compare < 0) return retrieveFromOffset(key, node.leftPointer());
        if (compare > 0) return retrieveFromOffset(key, node.rightPointer());
        return nodeStorage.readValue(offset);
    }

    @Override
    public void delete(K key) throws IOException {delete(key, nodeStorage.rootOffset(), 0, "");}
    private void delete(K key, long offset, long prevOffset, String prevLeftOrRight) throws IOException {
        if (nodeStorage.isNull(offset)) return;
        var node = nodeStorage.readKeyNode(offset);
        int compare = comparator.compare(key, node.key());
        if (compare < 0) delete(key, node.leftPointer(), offset, "L");
        else if (compare > 0) delete(key, node.rightPointer(), offset, "R");
        else {
            if (nodeStorage.isNull(node.leftPointer())){
                if (!nodeStorage.isNull(node.rightPointer())){
                    var rightChild = readFullEntry(node.rightPointer());
                    nodeStorage.writeNode(rightChild, offset);
                }
                else{
                    nodeStorage.deleteNode(offset);
                    nodeStorage.updatePointer(prevOffset, 0, prevLeftOrRight);
                }
                nodeStorage.updateStoredEntries(-1);
            }
            else if (nodeStorage.isNull(node.rightPointer())){
                TreeNodeDual<K, V> leftChild = readFullEntry(node.leftPointer());
                writeNewNode(leftChild, offset, -1);
            }
            else {
                var successor = leftMost(node.rightPointer());
                nodeStorage.updateNode(successor.key(), successor.value(), offset);
                nodeStorage.updateStoredEntries(-1);
                delete(successor.key(), node.rightPointer(), offset, "R");
            }
        }
    }

    private TreeNodeDual<K, V> readFullEntry(long offset) throws IOException {
        TreeNode<K> node = nodeStorage.readKeyNode(offset);
        V value = nodeStorage.readValue(offset);
        return new TreeNodeDual<K,V>(node, value);
    }

    private TreeNodeDual<K,V> leftMost(long offset) throws IOException {
        TreeNode<K> node = nodeStorage.readKeyNode(offset);
        if (nodeStorage.isNull(node.leftPointer())) {
            V value = nodeStorage.readValue(offset);
            return new TreeNodeDual<>(node, value);
        }
        return leftMost(node.leftPointer());
    }
}
