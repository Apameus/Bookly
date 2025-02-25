package gr.bookapp.storage.codec;

public record TreeNodeDual<K,V>(K key, long leftPointer, long rightPointer, V value) {

    public TreeNodeDual(K key, V value) {
        this(key, 0, 0, value);
    }

    public TreeNodeDual(TreeNode<K> keyNode, V value) {
        this(keyNode.key, keyNode.leftPointer, keyNode.rightPointer, value);
    }
}
