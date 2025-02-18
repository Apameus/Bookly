package codec;

import java.security.Key;

public final class TreeNode_Pointers<T> {
    public T data;
    public long leftChild;
    public long rightChild;

    public TreeNode_Pointers(T data) {
        this.data = data;
    }

    public TreeNode_Pointers(T data, long leftChild, long rightChild) {
        this.data = data;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }


}
