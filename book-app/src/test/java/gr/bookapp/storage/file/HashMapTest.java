package gr.bookapp.storage.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashMapTest {


    @Mock
    Comparator<Integer> comparator;
    @Mock
    NodeStorageMap<Integer, String> nodeStorage;
    @InjectMocks
    HashMap<Integer, String> hashMap;


    @Test
    @DisplayName("Retrieve with non-existing key")
    void retrieveWithNonExistingKey() {
        int key = 25;
        long offset = 25L;
        when(nodeStorage.calculateOffset(key)).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(true);
        assertThat(hashMap.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("Insert-Retrieve test")
    void insertRetrieveTest() {
        int key = 25;
        long offset = 25L;
        String value = "A";

        when(nodeStorage.calculateOffset(key)).thenReturn(offset);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offset)).thenReturn(true);
        hashMap.insert(key, value);
        verify(nodeStorage, times(1)).writeNode(key, value, offset);

        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.matchKey(offset, key)).thenReturn(true);
        when(nodeStorage.readValue(offset)).thenReturn(value);
        assertThat(hashMap.retrieve(key)).isEqualTo(value);
    }

    @Test
    @DisplayName("Insert-Retrieve from Collision")
    void insertRetrieveFromCollision() {
        long offsetA = 25L;
        int keyA = 25;
        String valueA = "A";

        long offsetB = 50L;
        int keyB = 50;
        String valueB = "B";

        when(nodeStorage.calculateOffset(keyA)).thenReturn(offsetA);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offsetA)).thenReturn(true);
        hashMap.insert(keyA, valueA);
        verify(nodeStorage, times(1)).writeNode(keyA, valueA, offsetA);

        when(nodeStorage.calculateOffset(keyB)).thenReturn(offsetA);
        when(nodeStorage.isNull(offsetA)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyB)).thenReturn(false);
        when(nodeStorage.readNextOffset(offsetA)).thenReturn(0L);
        when(nodeStorage.isNull(0L)).thenReturn(true);
        when(nodeStorage.findEmptySlot(0L)).thenReturn(offsetB);
        when(nodeStorage.isNull(offsetB)).thenReturn(true);
        hashMap.insert(keyB, valueB);
        verify(nodeStorage, times(1)).updateNextOffset(offsetA, offsetB);
        verify(nodeStorage, times(2)).updateStoredEntries(+1);

        when(nodeStorage.isNull(offsetA)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyA)).thenReturn(true);
        when(nodeStorage.readValue(offsetA)).thenReturn(valueA);
        assertThat(hashMap.retrieve(keyA)).isEqualTo(valueA);

        when(nodeStorage.isNull(offsetB)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyB)).thenReturn(false);
        when(nodeStorage.readNextOffset(offsetA)).thenReturn(offsetB);
        when(nodeStorage.matchKey(offsetB, keyB)).thenReturn(true);
        when(nodeStorage.readValue(offsetB)).thenReturn(valueB);
        assertThat(hashMap.retrieve(keyB)).isEqualTo(valueB);
    }

    @Test
    @DisplayName("Deletion test")
    void deletionTest() {
        int key = 25;
        long offset = 25L;
        String value = "A";

        when(nodeStorage.calculateOffset(key)).thenReturn(offset);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offset)).thenReturn(true);
        hashMap.insert(key, value);

        when(nodeStorage.matchKey(offset, key)).thenReturn(true);
        hashMap.delete(key);
        verify(nodeStorage, times(1)).deleteNode(offset);
        verify(nodeStorage, times(1)).updateStoredEntries(-1);
    }

    @Test
    @DisplayName("Deletion from collision")
    void deletionFromCollision() {
        long offsetA = 25L;
        int keyA = 25;
        String valueA = "A";

        long offsetB = 50L;
        int keyB = 50;
        String valueB = "B";

        when(nodeStorage.calculateOffset(keyA)).thenReturn(offsetA);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offsetA)).thenReturn(true);
        hashMap.insert(keyA, valueA);

        when(nodeStorage.calculateOffset(keyB)).thenReturn(offsetA);
        when(nodeStorage.isNull(offsetA)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyB)).thenReturn(false);
        when(nodeStorage.readNextOffset(offsetA)).thenReturn(0L);
        when(nodeStorage.isNull(0L)).thenReturn(true);
        when(nodeStorage.findEmptySlot(0L)).thenReturn(offsetB);
        when(nodeStorage.isNull(offsetB)).thenReturn(true);
        hashMap.insert(keyB, valueB);

        when(nodeStorage.readNextOffset(offsetA)).thenReturn(offsetB);
        when(nodeStorage.isNull(offsetB)).thenReturn(false);
        when(nodeStorage.matchKey(offsetB, keyB)).thenReturn(true);
        hashMap.delete(keyB);
        verify(nodeStorage, times(1)).deleteNode(offsetB);
        verify(nodeStorage, times(1)).updateStoredEntries(-1);
    }



}