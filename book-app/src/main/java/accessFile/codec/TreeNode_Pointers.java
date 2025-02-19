package accessFile.codec;

public final class TreeNode_Pointers<T> {
    public T data;
    public long leftPointer;
    public long rightPointer;

    public TreeNode_Pointers(T data) {
        this.data = data;
    }

    public TreeNode_Pointers(T data, long leftChild, long rightChild) {
        this.data = data;
        this.leftPointer = leftChild;
        this.rightPointer = rightChild;
    }


}
