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
    NodeStorage_Map<Integer, String> nodeStorage;
    @InjectMocks
    HashMap<Integer, String> hashMap;


    @Test
    @DisplayName("Retrieve with non-existing key")
    void retrieveWithNonExistingKey() {
        int key = 15;
        long offset = 25L;
        when(nodeStorage.calculateOffset(key)).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(true);
        assertThat(hashMap.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("Insert-Retrieve test")
    void insertRetrieveTest() {
        int key = 15;
        long offset = 25L;
        when(nodeStorage.calculateOffset(key)).thenReturn(offset);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offset)).thenReturn(true);
//        nodeStorage.writeNode();
        hashMap.insert(key, "A");

        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.matchKey(offset, key)).thenReturn(true);
        when(nodeStorage.readValue(offset)).thenReturn("A");
        assertThat(hashMap.retrieve(key)).isEqualTo("A");
    }

    @Test
    @DisplayName("Insert-Retrieve from Collision")
    void insertRetrieveFromCollision() {
        long offsetA = 25L;
        int keyA = 10;
        long offsetB = 50L;
        int keyB = 20;
        when(nodeStorage.calculateOffset(keyA)).thenReturn(offsetA);
        when(nodeStorage.isFull()).thenReturn(false);
        when(nodeStorage.isNull(offsetA)).thenReturn(true);
//        nodeStorage.writeNode();
        hashMap.insert(keyA, "A");

        when(nodeStorage.calculateOffset(keyB)).thenReturn(offsetA);
        when(nodeStorage.isNull(offsetA)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyB)).thenReturn(false);
        when(nodeStorage.readNextOffset(offsetA)).thenReturn(0L);
        when(nodeStorage.isNull(0L)).thenReturn(true);
        when(nodeStorage.findEmptySlot(0L)).thenReturn(offsetB);
        when(nodeStorage.isNull(offsetB)).thenReturn(true);
//        nodeStorage.updateNextOffset(offsetA, offsetB);
        hashMap.insert(keyB, "B");
        verify(nodeStorage, times(2)).updateStoredEntries(+1);

        when(nodeStorage.isNull(offsetA)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyA)).thenReturn(true);
        when(nodeStorage.readValue(offsetA)).thenReturn("A");
        assertThat(hashMap.retrieve(keyA)).isEqualTo("A");

        when(nodeStorage.isNull(offsetB)).thenReturn(false);
        when(nodeStorage.matchKey(offsetA, keyB)).thenReturn(false);
        when(nodeStorage.readNextOffset(offsetA)).thenReturn(offsetB);
        when(nodeStorage.matchKey(offsetB, keyB)).thenReturn(true);
        when(nodeStorage.readValue(offsetB)).thenReturn("B");
        assertThat(hashMap.retrieve(keyB)).isEqualTo("B");
    }



}