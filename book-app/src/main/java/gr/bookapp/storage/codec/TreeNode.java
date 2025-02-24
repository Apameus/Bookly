package gr.bookapp.storage.codec;

public class TreeNode<K> {

    public K key;
    public long leftPointer;
    public long rightPointer;

    public TreeNode(K key) {
        this.key = key;
    }

    public TreeNode(K key, long leftPointer, long rightPointer) {
        this.key = key;
        this.leftPointer = leftPointer;
        this.rightPointer = rightPointer;
    }
}
