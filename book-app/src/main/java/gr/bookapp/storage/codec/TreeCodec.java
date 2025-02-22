package gr.bookapp.storage.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record TreeCodec<K,V>(Codec<K> keyCodec, Codec<V> valueCodec) implements Codec<TreeNodeDual<K,V>>{

    @Override
    public int maxByteSize() {
        return keyCodec.maxByteSize() + Long.BYTES + Long.BYTES + valueCodec().maxByteSize();
    }

    @Override
    public TreeNodeDual<K, V> read(RandomAccessFile accessFile) throws IOException {
        return new TreeNodeDual<>(keyCodec.read(accessFile), accessFile.readLong(), accessFile.readLong(), valueCodec.read(accessFile));
    }

    public TreeNode<K> readKeyEntry(RandomAccessFile accessFile) throws IOException {
        return new TreeNode<>(keyCodec.read(accessFile), accessFile.readLong(), accessFile.readLong());
    }

    @Override
    public void write(RandomAccessFile accessFile, TreeNodeDual<K, V> obj) throws IOException {
        keyCodec.write(accessFile, obj.key);
        accessFile.writeLong(obj.leftPointer);
        accessFile.writeLong(obj.rightPointer);
        valueCodec.write(accessFile, obj.value);
    }

    public void write(RandomAccessFile accessFile, K key, V value) throws IOException {
        write(accessFile, new TreeNodeDual<>(key, value));
    }
}
