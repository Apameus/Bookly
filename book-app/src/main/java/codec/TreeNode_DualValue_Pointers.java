package codec;

public final class TreeNode_DualValue_Pointers<K, V> {
    public K key;
    public Long leftChild;
    public Long rightChild;
    public V value;

    public TreeNode_DualValue_Pointers(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public TreeNode_DualValue_Pointers(K key, Long leftChild, Long rightChild, V value) {
        this(key, value);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public TreeNode_DualValue_Pointers(TreeNode_Pointers<K> treeNodeSingleValue, V value) {
        this(treeNodeSingleValue.data, treeNodeSingleValue.leftChild, treeNodeSingleValue.rightChild, value);
    }
}
