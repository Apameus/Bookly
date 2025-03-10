package gr.bookapp.database;

public interface Index<T, K> {

    K extractKey(T obj);

}
