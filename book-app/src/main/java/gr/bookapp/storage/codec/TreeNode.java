package gr.bookapp.storage.codec;

public record TreeNode<K>(K key, long leftPointer, long rightPointer) {

    public TreeNode(K key) {
        this(key, 0, 0);
    }

}