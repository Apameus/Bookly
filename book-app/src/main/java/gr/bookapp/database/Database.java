package gr.bookapp.database;

import gr.bookapp.storage.file.ObjectTable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Database<PM, T> {
    ObjectTable<PM, T> objectTable;
    List<Index<T, ?>> indexes;

    public Database(ObjectTable<PM, T> objectTable) {
        this.objectTable = objectTable;
    }

    public <K> List<T> findAllBy(Index<T, K> index, K key){
        ArrayList<T> list = new ArrayList<>();
        for (var obj : objectTable){
            K indexKey = index.extractKey(obj.getValue()); //TODO getKey()
            if (indexKey.equals(key)) list.add(obj.getValue()); //TODO getKey()
        }
        return list;
    }

    public T retrieve(PM key){
        return objectTable.retrieve(key);
    }

    public void insert(PM key, T value){
        objectTable.insert(key, value);
    }

    public void delete(PM key){
        objectTable.delete(key);
    }

    public Iterator<Map.Entry<PM, T>> entryIterator(){
        return objectTable.iterator();
    }
}
