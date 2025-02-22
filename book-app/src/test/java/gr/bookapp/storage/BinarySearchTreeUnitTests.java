package gr.bookapp.storage;

import gr.bookapp.storage.model.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BinarySearchTreeUnitTests {

    @Mock
    Comparator<Integer> comparator;
    @Mock
    NodeStorage<Integer, Integer> nodeStorage;
    @InjectMocks
    BinarySearchTree<Integer, Integer> binarySearchTree;

    @Test
    @DisplayName("If BinarySearchTree root is null, then retrieve should always return null")
    void ifBinarySearchTreeRootIsNullThenRetrieveShouldAlwaysReturnNull() {
        long rootOffset = 500L;
        when(nodeStorage.rootOffset())
                .thenReturn(rootOffset);

        when(nodeStorage.isNull(rootOffset)).thenReturn(true);

        assertThat(binarySearchTree.retrieve(50)).isNull();
    }

    @Test
    @DisplayName("test")
    void test() {
        int key = 20;
        long offset = 500L;
        Node<Integer> node = new Node<Integer>(20, 0 ,0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);


        when(nodeStorage.readNode(offset)).thenReturn(node);
        when(comparator.compare(key, node.key())).thenReturn(0);
        when(nodeStorage.readValue(offset)).thenReturn(450);

        assertThat(binarySearchTree.retrieve(key)).isEqualTo(450);
    }

    @Test
    @DisplayName("If retrieve with key is greater than found.key and found.right is null then return null")
    void test2() {
        int key = 20;
        long offset = 500;
        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);

        Node<Integer> node = new Node<>(19, 0, 0);
        when(nodeStorage.readNode(offset)).thenReturn(node);
        when(comparator.compare(key, node.key())).thenReturn(1);

        when(nodeStorage.isNull(0)).thenReturn(true);
        assertThat(binarySearchTree.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("If retrieve with key lower than found.key and found.left has the value for retrieval")
    void ifKeyIsLowerThanFoundKeyAndFoundLeftHasTheValueForRetrieval() {

        int key = 20;
        long rootOffset = 500L;
        when(nodeStorage.rootOffset()).thenReturn(rootOffset);
        when(nodeStorage.isNull(rootOffset)).thenReturn(false);

        int rootNodeKey = 19;
        long secondNodeOffset = 700L;
        Node<Integer> rootNode = new Node<>(rootNodeKey, secondNodeOffset, 0);
        when(nodeStorage.readNode(rootOffset)).thenReturn(rootNode);
        when(comparator.compare(key, rootNode.key())).thenReturn(-1);

        Node<Integer> secondNode = new Node<>(key, 0, 0);
        when(nodeStorage.isNull(secondNodeOffset)).thenReturn(false);
        when(nodeStorage.readNode(secondNodeOffset)).thenReturn(secondNode);
        when(comparator.compare(key, secondNode.key())).thenReturn(0);
        when(nodeStorage.readValue(secondNodeOffset)).thenReturn(secondNode.key());

        assertThat(binarySearchTree.retrieve(key)).isEqualTo(secondNode.key());
    }

    @Test
    @DisplayName("Insertion test")
    void insertionTest() {

    }


}