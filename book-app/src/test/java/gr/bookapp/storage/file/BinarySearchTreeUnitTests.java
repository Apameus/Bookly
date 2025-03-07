package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.TreeNode;
import gr.bookapp.storage.codec.TreeNodeDual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BinarySearchTreeUnitTests {

    @Mock
    Comparator<Integer> comparator;
    @Mock
    NodeStorageTree<Integer, Integer> nodeStorage;
    @InjectMocks
    BinarySearchTree<Integer, Integer> binarySearchTree;

    @Test
    @DisplayName("If BinarySearchTree root is null, then retrieve should always return null")
    void ifBinarySearchTreeRootIsNullThenRetrieveShouldAlwaysReturnNull() throws IOException {
        long rootOffset = 500L;
        when(nodeStorage.rootOffset())
                .thenReturn(rootOffset);

        when(nodeStorage.isNull(rootOffset)).thenReturn(true);

        assertThat(binarySearchTree.retrieve(50)).isNull();
    }

    @Test
    @DisplayName("Retrieve from root")
    void test() throws IOException {
        int key = 20;
        long offset = 500L;
        TreeNode<Integer> node = new TreeNode<Integer>(20, 0 ,0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);


        when(nodeStorage.readKeyNode(offset)).thenReturn(node);
        when(comparator.compare(key, node.key())).thenReturn(0);
        when(nodeStorage.readValue(offset)).thenReturn(450);

        assertThat(binarySearchTree.retrieve(key)).isEqualTo(450);
    }

    @Test
    @DisplayName("If retrieve with key is greater than found.key and found.right is null then return null")
    void test2() throws IOException {
        int key = 20;
        long offset = 500;
        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);

        TreeNode<Integer> node = new TreeNode<>(19, 0, 0);
        when(nodeStorage.readKeyNode(offset)).thenReturn(node);
        when(comparator.compare(key, node.key())).thenReturn(1);

        when(nodeStorage.isNull(0)).thenReturn(true);
        assertThat(binarySearchTree.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("If retrieve with key lower than found.key and found.left has the value for retrieval")
    void ifKeyIsLowerThanFoundKeyAndFoundLeftHasTheValueForRetrieval() throws IOException {

        int key = 20;
        long rootOffset = 500L;
        when(nodeStorage.rootOffset()).thenReturn(rootOffset);
        when(nodeStorage.isNull(rootOffset)).thenReturn(false);

        int rootNodeKey = 19;
        long secondNodeOffset = 700L;
        TreeNode<Integer> rootNode = new TreeNode<>(rootNodeKey, secondNodeOffset, 0);
        when(nodeStorage.readKeyNode(rootOffset)).thenReturn(rootNode);
        when(comparator.compare(key, rootNode.key())).thenReturn(-1);

        TreeNode<Integer> secondNode = new TreeNode<>(key, 0, 0);
        when(nodeStorage.isNull(secondNodeOffset)).thenReturn(false);
        when(nodeStorage.readKeyNode(secondNodeOffset)).thenReturn(secondNode);
        when(comparator.compare(key, secondNode.key())).thenReturn(0);
        when(nodeStorage.readValue(secondNodeOffset)).thenReturn(secondNode.key());

        assertThat(binarySearchTree.retrieve(key)).isEqualTo(secondNode.key());
    }

    @Test
    @DisplayName("Insert instant")
    void insertionTest() throws IOException {
        long offset = 500;
        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(true);

        binarySearchTree.insert(20, 20);
        verify(nodeStorage, times(1)).writeNode(new TreeNodeDual<>(20,20), offset);
    }

    @Test
    @DisplayName("Insert with already existing node have less key")
    void insertWithAlreadyExistingNodeHaveLessKey() throws IOException {
        long offset = 500L;
        TreeNode<Integer> foundNode = new TreeNode<>(19, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(foundNode);

        when(comparator.compare(20, foundNode.key())).thenReturn(1);
        when(nodeStorage.isNull(foundNode.rightPointer())).thenReturn(true);
        when(nodeStorage.findEmptySlot()).thenReturn(550L);
                // updatePointer

        binarySearchTree.insert(20,20);

        verify(nodeStorage, times(1)).writeNode(new TreeNodeDual<>(20,20), 550L);
        // verify storedEntries
        // verify updatePointer
    }

    @Test
    @DisplayName("Insert with already 2 existing nodes having greater keys")
    void insertWithAlready2ExistingNodesHavingGreaterKeys() throws IOException {
        long offset = 500L;
        TreeNode<Integer> foundNode = new TreeNode<>(22, 550, 0);
        TreeNode<Integer> secondFoundNode = new TreeNode<>(21, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(foundNode);
        when(comparator.compare(20, foundNode.key())).thenReturn(-1);

        when(nodeStorage.isNull(foundNode.leftPointer())).thenReturn(false);
        when(nodeStorage.readKeyNode(foundNode.leftPointer())).thenReturn(secondFoundNode);
        when(comparator.compare(20, secondFoundNode.key())).thenReturn(-1);

        when(nodeStorage.isNull(secondFoundNode.leftPointer())).thenReturn(true);
        when(nodeStorage.findEmptySlot()).thenReturn(600L);


        binarySearchTree.insert(20,20);
        verify(nodeStorage, times(1)).writeNode(new TreeNodeDual<>(20,20) , 600L);

    }

    @Test
    @DisplayName("delete root")
    void deleteRoot() throws IOException {
        long offset = 500L;
        TreeNode<Integer> node = new TreeNode<>(20, 0, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(node);

        when(comparator.compare(20, node.key())).thenReturn(0);
        when(nodeStorage.isNull(node.leftPointer())).thenReturn(true);
        when(nodeStorage.isNull(node.rightPointer())).thenReturn(true);

        binarySearchTree.delete(20);
        verify(nodeStorage, times(1)).deleteNode(offset);
    }

    @Test
    @DisplayName("Delete node with only right child")
    void deleteNodeWithOnlyRightChild() throws IOException {
        long offset = 500l;
        TreeNode<Integer> node = new TreeNode<>(20, 0, 550);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);

        when(nodeStorage.isNull(node.leftPointer())).thenReturn(true);
        when(nodeStorage.isNull(node.rightPointer())).thenReturn(false);
        TreeNode<Integer> rightChild = new TreeNode<>(25, 0, 0);
        int rightChildValue = 250;
        when(nodeStorage.readKeyNode(node.rightPointer())).thenReturn(rightChild);
        when(nodeStorage.readValue(node.rightPointer())).thenReturn(rightChildValue);

        binarySearchTree.delete(20);

        verify(nodeStorage, times(1)).writeNode(new TreeNodeDual<>(rightChild.key(), rightChildValue), offset);
    }

    @Test
    @DisplayName("Delete node with only leftChild")
    void deleteNodeWithOnlyLeftChild() throws IOException {
        long offset = 500L;
        TreeNode<Integer> node = new TreeNode<>(20, 550, 0);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);
        when(nodeStorage.isNull(node.leftPointer())).thenReturn(false);

        TreeNode<Integer> leftChild = new TreeNode<>(18, 0, 0);
        int leftChildValue = 180;
        when(nodeStorage.isNull(node.rightPointer())).thenReturn(true);
        when(nodeStorage.readKeyNode(node.leftPointer())).thenReturn(leftChild);
        when(nodeStorage.readValue(node.leftPointer())).thenReturn(leftChildValue);

        binarySearchTree.delete(20);

        verify(nodeStorage, times(1)).writeNode(new TreeNodeDual<>(leftChild.key(), leftChildValue), offset);
    }

    @Test
    @DisplayName("delete node with both children")
    void deleteNodeWithBothChildren() throws IOException {
        long offset = 500L;
        TreeNode<Integer> node = new TreeNode<>(20, 550, 600);

        when(nodeStorage.rootOffset()).thenReturn(offset);
        when(nodeStorage.isNull(offset)).thenReturn(false);
        when(nodeStorage.readKeyNode(offset)).thenReturn(node);
        when(comparator.compare(20, node.key())).thenReturn(0);

        when(nodeStorage.isNull(node.leftPointer())).thenReturn(false);
        when(nodeStorage.isNull(node.rightPointer())).thenReturn(false);

        TreeNode<Integer> rightChild = new TreeNode<>(25, 650, 0);
        when(nodeStorage.readKeyNode(node.rightPointer())).thenReturn(rightChild);
        when(nodeStorage.isNull(rightChild.leftPointer())).thenReturn(false);

        TreeNode<Integer> successor = new TreeNode<>(22, 0, 0);
        int successorValue = 22;
        when(nodeStorage.readKeyNode(rightChild.leftPointer())).thenReturn(successor);
        when(nodeStorage.isNull(successor.leftPointer())).thenReturn(true);
        when(nodeStorage.readValue(rightChild.leftPointer())).thenReturn(successorValue);


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