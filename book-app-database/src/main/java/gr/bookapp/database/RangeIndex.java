package gr.bookapp.database;

import java.util.Comparator;
import java.util.function.Function;

public interface RangeIndex<T,K> extends Index<T, K>{

    static <T, K> RangeIndex<T, K> of(Function<T, K> extractKey,
                                      Comparator<K> comparator){
        return new RangeIndex<T, K>() {
            @Override
            public Comparator<K> comparator() {
                return comparator;
            }

            @Override
            public K extractKey(T obj) {
                return extractKey.apply(obj);
            }
        };
    }

    Comparator<K> comparator();

}
