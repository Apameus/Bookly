package gr.bookapp.database;

import java.util.function.Function;

public interface Index<T, K> {

    static <T,K> Index<T,K> of(Function<T,K> extractKey) {
        return extractKey::apply;
    }



    K extractKey(T obj);
}
