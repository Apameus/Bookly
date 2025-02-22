package gr.bookapp.storage.model;

public record Node<K>(K key, long leftChild, long rightChild) {

}
