package gr.bookapp.storage;

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
    BinarySearchTree.NodeStorage<Integer, Integer> nodeStorage;
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
}