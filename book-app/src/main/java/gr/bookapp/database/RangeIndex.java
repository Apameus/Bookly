package gr.bookapp.database;

import java.util.Iterator;

public interface RangeIndex<T,K> extends Index<T, K>{
    Iterator<T> objectsInRange(K min, K max);
}
