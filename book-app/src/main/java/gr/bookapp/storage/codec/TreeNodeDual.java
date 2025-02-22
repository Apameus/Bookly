package gr.bookapp.storage.codec;

public final class TreeNodeDual<K,V> {

    K key;
    long leftPointer;
    long rightPointer;
    V value;

    public TreeNodeDual(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public TreeNodeDual(K key, long leftPointer, long rightPointer, V value) {
        this(key,value);
        this.leftPointer = leftPointer;
        this.rightPointer = rightPointer;
    }

    public TreeNodeDual(TreeNode<K> keyNode, V value) {
        this(keyNode.key, keyNode.leftPointer, keyNode.rightPointer, value);
    }
}
