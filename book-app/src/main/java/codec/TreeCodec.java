package codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record TreeCodec<T>(Codec<T> genericDataCodec) implements Codec<TreeNode_Pointers<T>> {

    @Override
    public int maxByteSize() { return genericDataCodec.maxByteSize() + Long.BYTES + Long.BYTES; } // genericValueSize + ChildRef + ChildRef

    @Override
    public void write(RandomAccessFile accessFile, TreeNode_Pointers<T> node) throws IOException {
        genericDataCodec.write(accessFile, node.data);
        accessFile.writeLong(node.leftPointer);
        accessFile.writeLong(node.rightPointer);
    }

    @Override
    public TreeNode_Pointers<T> read(RandomAccessFile accessFile) throws IOException {
        T genericData = genericDataCodec.read(accessFile);
        long leftChild = accessFile.readLong();
        long rightChild = accessFile.readLong();
        return new TreeNode_Pointers<>(genericData, leftChild, rightChild);
    }
}
