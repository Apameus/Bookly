package accessFile.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record TreeCodecDual<K,V>(Codec<K> keyCodec, Codec<V> valueCodec) implements Codec<TreeNode_DualValue_Pointers<K,V>>{
    @Override
    public int maxByteSize() {
        return keyCodec.maxByteSize() + Long.BYTES + Long.BYTES + valueCodec.maxByteSize();
    }

    @Override
    public TreeNode_DualValue_Pointers<K, V> read(RandomAccessFile accessFile) throws IOException {
        return new TreeNode_DualValue_Pointers<>(keyCodec.read(accessFile), accessFile.readLong(), accessFile.readLong(), valueCodec().read(accessFile));
    }

    public TreeNode_Pointers<K> readKeyNode(RandomAccessFile accessFile) throws IOException{
        return new TreeNode_Pointers<>(keyCodec.read(accessFile), accessFile.readLong(), accessFile.readLong());
    }

    @Override
    public void write(RandomAccessFile accessFile, TreeNode_DualValue_Pointers<K, V> dualTreeNode) throws IOException {
        keyCodec.write(accessFile, dualTreeNode.key);
        accessFile.writeLong(dualTreeNode.leftChild);
        accessFile.writeLong(dualTreeNode.rightChild);
        valueCodec.write(accessFile, dualTreeNode.value);
    }

    public void write(RandomAccessFile accessFile, K key, V value) throws IOException {
        write(accessFile, new TreeNode_DualValue_Pointers<>(key, value));
    }
}
