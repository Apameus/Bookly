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

    public List<T> findAll(){
        ArrayList<T> objects = new ArrayList<>();
        for (var obj : objectTable) {
            objects.add(obj.getValue());
        }
        return objects;
    }

    public <K> List<T> findAllByIndex(Index<T, K> index, K key){
        ArrayList<T> list = new ArrayList<>();
        for (var obj : objectTable){
            K indexKey = index.extractKey(obj.getValue());
            if (indexKey.equals(key)) list.add(obj.getValue());
        }
        return list;
    }

    public <K> List<T> findAllByIndexWithKeys(Index<T, List<K>> index, K key){ //TODO !!!
        ArrayList<T> list = new ArrayList<>();
        for (var obj : objectTable){
            List<K> indexKeys = index.extractKey(obj.getValue()); //list K
            if (indexKeys.contains(key)) list.add(obj.getValue());
        }
        return list;
    }

    public <K> List<T> findAllInRange(RangeIndex<T, K> index, K min, K max){
        var result = new ArrayList<T>();
        for (var obj : objectTable) {
            K indexKey = index.extractKey(obj.getValue());
            if (index.comparator().compare(indexKey, min) >= 0 && index.comparator().compare(indexKey, max) <= 0)
                result.add(obj.getValue());
        }
        return result;
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
