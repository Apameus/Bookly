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
import static org.mockito.Mockito.*;

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
    @DisplayName("Insert instant")
    void insertionTest() {
        long offset = 500;
        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(true);

        binarySearchTree.insert(20, 20);
        verify(nodeStorage, times(1)).writeNewNode(20,20, offset);
    }

    @Test
    @DisplayName("Insert with already existing node have less key")
    void insertWithAlreadyExistingNodeHaveLessKey() {
        long offset = 500L;
        Node<Integer> foundNode = new Node<>(19, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(foundNode);

        when(comparator.compare(20, foundNode.key())).thenReturn(1);
        when(nodeStorage.isNull(foundNode.rightChild())).thenReturn(true);

        binarySearchTree.insert(20,20);

        // To be noted: the writeNewNode's offset can't be 0! since the childPointer is null we have to find the next empty slot.
        verify(nodeStorage, times(1)).writeNewNode(20,20, foundNode.rightChild());
    }

    @Test
    @DisplayName("Insert with already 2 existing nodes having greater keys")
    void insertWithAlready2ExistingNodesHavingGreaterKeys() {
        long offset = 500L;
        Node<Integer> foundNode = new Node<>(22, 550, 0);
        Node<Integer> secondFoundNode = new Node<>(21, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(foundNode);
        when(comparator.compare(20, foundNode.key())).thenReturn(-1);

        when(nodeStorage.isNull(foundNode.leftChild())).thenReturn(false);
        when(nodeStorage.readNode(foundNode.leftChild())).thenReturn(secondFoundNode);
        when(comparator.compare(20, secondFoundNode.key())).thenReturn(-1);

        when(nodeStorage.isNull(secondFoundNode.leftChild())).thenReturn(true);


        binarySearchTree.insert(20,20);
        verify(nodeStorage, times(1)).writeNewNode(20,20, secondFoundNode.leftChild());

    }

    @Test
    @DisplayName("delete root")
    void deleteRoot() {
        long offset = 500L;
        Node<Integer> node = new Node<>(20, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(node);

        when(comparator.compare(20, node.key())).thenReturn(0);
        when(nodeStorage.isNull(node.leftChild())).thenReturn(true);
        when(nodeStorage.isNull(node.rightChild())).thenReturn(true);

        binarySearchTree.delete(20);
        verify(nodeStorage, times(1)).deleteNode(offset);
    }

    @Test
    @DisplayName("Delete node with only right child")
    void deleteNodeWithOnlyRightChild() {
        long offset = 500l;
        Node<Integer> node = new Node<>(20, 0, 550);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);

        when(nodeStorage.isNull(node.leftChild())).thenReturn(true);
        when(nodeStorage.isNull(node.rightChild())).thenReturn(false);
        Node<Integer> rightChild = new Node<>(25, 0, 0);
        int rightChildValue = 250;
        when(nodeStorage.readNode(node.rightChild())).thenReturn(rightChild);
        when(nodeStorage.readValue(node.rightChild())).thenReturn(rightChildValue);

        binarySearchTree.delete(20);

        verify(nodeStorage, times(1)).updateNode(rightChild.key(), rightChildValue, offset);
    }

    @Test
    @DisplayName("Delete node with only leftChild")
    void deleteNodeWithOnlyLeftChild() {
        long offset = 500L;
        Node<Integer> node = new Node<>(20, 550, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);
        when(nodeStorage.isNull(node.leftChild())).thenReturn(false);

        Node<Integer> leftChild = new Node<>(18, 0, 0);
        int leftChildValue = 180;
        when(nodeStorage.isNull(node.rightChild())).thenReturn(true);
        when(nodeStorage.readNode(node.leftChild())).thenReturn(leftChild);
        when(nodeStorage.readValue(node.leftChild())).thenReturn(leftChildValue);

        binarySearchTree.delete(20);

        verify(nodeStorage, times(1)).updateNode(leftChild.key(), leftChildValue, offset);
    }

    @Test
    @DisplayName("delete node with both children")
    void deleteNodeWithBothChildren() {
        long offset = 500L;
        Node<Integer> node = new Node<>(20, 550, 600);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);

        when(nodeStorage.isNull(node.leftChild())).thenReturn(false);
        when(nodeStorage.isNull(node.rightChild())).thenReturn(false);

        Node<Integer> rightChild = new Node<>(25, 650, 0);
        when(nodeStorage.readNode(node.rightChild())).thenReturn(rightChild);
        when(nodeStorage.isNull(rightChild.leftChild())).thenReturn(false);

        Node<Integer> successor = new Node<>(22, 0, 0);
        int successorValue = 22;
        when(nodeStorage.readNode(rightChild.leftChild())).thenReturn(successor);
        when(nodeStorage.isNull(successor.leftChild())).thenReturn(true);
        when(nodeStorage.readValue(rightChild.leftChild())).thenReturn(successorValue);


        // + delete successor
//        when(nodeStorage.isNull(rightChild.leftChild())).thenReturn(false);
//        when(nodeStorage.readNode(rightChild.leftChild())).thenReturn(successor);
//        when(comparator.compare(22, successor.key())).thenReturn(0);
//        when(nodeStorage.isNull(successor.leftChild())).thenReturn(true);
//        when(nodeStorage.isNull(successor.rightChild())).thenReturn(true);
                //
        binarySearchTree.delete(20);
        verify(nodeStorage, times(1)).updateNode(successor.key(), successorValue, offset);
//        verify(nodeStorage, times(1)).deleteNode(rightChild.leftChild());
    }


}