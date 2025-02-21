package gr.bookapp.storage;

import java.util.Comparator;

public final class BinarySearchTree<K, V> implements ObjectTable<K, V> {

    private final Comparator<K> comparator;
    private final NodeStorage<K, V> nodeStorage;

    public BinarySearchTree(Comparator<K> comparator, NodeStorage<K, V> nodeStorage) {
        this.comparator = comparator;
        this.nodeStorage = nodeStorage;
    }

    public interface NodeStorage<K, V> {

        boolean isNull(long nodeOffset);

        Node<K> readNode(long nodeOffset);

        V readValue(long nodeOffset);

        long rootOffset();
    }

    public record Node<K>(long leftChild, long rightChild, K key){

    }

    @Override
    public void insert(K key, V value) {

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
        int compare = comparator.compare(key, node.key);
        if (compare > 0) {
            // key > node.key
            return retrieveFromOffset(key, node.rightChild());
        }
        if (compare < 0){
            // key < node.key
            return retrieveFromOffset(key, node.leftChild());
        }
        // key == node.key
        return nodeStorage.readValue(offset);
    }

    @Override
    public void delete(K key) {

    }
}
