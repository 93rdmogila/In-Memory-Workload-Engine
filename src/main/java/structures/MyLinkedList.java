package structures;
public class MyLinkedList {
    private static final class Node {
        private int value;
        private Node next;
        private Node prev;
        private Node(int value) {
            this.value = value;
        }
    }
    private Node head;
    private Node tail;
    private int size;

    public int size() {
        return size;
    }

    public void add(int value, Metrics metrics) {
        Node newNode = new Node(value);

        if (tail == null) {
            head = newNode;
            tail = newNode;
            metrics.addMoves(2);
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            metrics.addMoves(3);
        }
        size++;
    }

    public void add(int index, int value, Metrics metrics) {
        checkPositionIndex(index);

        if (index == size) {
            add(value, metrics);
            return;
        }

        Node newNode = new Node(value);

        if (index == 0) {
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
            size++;
            metrics.addMoves(2);
            return;
        }

        Node nextNode = getNode(index, metrics);
        Node previousNode = nextNode.prev;

        previousNode.next = newNode;
        newNode.prev = previousNode;
        newNode.next = nextNode;
        nextNode.prev = newNode;
        size++;
        metrics.addMoves(4);
    }

    public int remove(int index, Metrics metrics) {
        checkElementIndex(index);

        Node node = getNode(index, metrics);
        int removed = node.value;
        metrics.addSteps(1);

        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        metrics.addMoves(2);
        size--;
        return removed;
    }

    public int get(int index, Metrics metrics) {
        Node node = getNode(index, metrics);
        metrics.addSteps(1);
        return node.value;
    }

    public boolean contains(int value, Metrics metrics) {
        Node current = head;

        while (current != null) {
            metrics.addSteps(1);
            metrics.addComparisons(1);

            if (current.value == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private Node getNode(int index, Metrics metrics) {
        checkElementIndex(index);

        if (index < size / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
                metrics.addSteps(1); 
            }
            return current;
        }

        Node current = tail;
        for (int i = size - 1; i > index; i--) {
            current = current.prev;
            metrics.addSteps(1);
        }
        return current;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }
}
