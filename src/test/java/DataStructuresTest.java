import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structures.DynamicArray;
import structures.Metrics;
import structures.MinHeap;
import structures.MyLinkedList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DataStructuresTest {
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
    }

    @Test
    void dynamicArrayMatchesArrayListOnRandomOperations() {
        DynamicArray actual = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 1_000; i++) {
            int operation = random.nextInt(4);
            if (expected.isEmpty() || operation == 0) {
                int value = random.nextInt();
                actual.add(value, metrics);
                expected.add(value);
            } else if (operation == 1) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index), actual.get(index, metrics));
            } else if (operation == 2) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt();
                actual.add(index, value, metrics);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index), actual.remove(index, metrics));
            }

            assertEquals(expected.size(), actual.size());
            for (int j = 0; j < expected.size(); j++) {
                assertEquals(expected.get(j), actual.get(j, metrics));
            }
        }
    }

    @Test
    void linkedListMatchesLinkedListOnRandomOperations() {
        MyLinkedList actual = new MyLinkedList();
        LinkedList<Integer> expected = new LinkedList<>();
        Random random = new Random(42);

        for (int i = 0; i < 1_000; i++) {
            int operation = random.nextInt(4);
            if (expected.isEmpty() || operation == 0) {
                int value = random.nextInt();
                actual.add(value, metrics);
                expected.add(value);
            } else if (operation == 1) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index), actual.get(index, metrics));
            } else if (operation == 2) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt();
                actual.add(index, value, metrics);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index), actual.remove(index, metrics));
            }

            assertEquals(expected.size(), actual.size());
            for (int j = 0; j < expected.size(); j++) {
                assertEquals(expected.get(j), actual.get(j, metrics));
            }
        }
    }

    @Test
    void minHeapMatchesPriorityQueue() {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int i = 0; i < 2_000; i++) {
            int value = random.nextInt(10_000);
            actual.insert(value, metrics);
            expected.add(value);
            assertEquals(expected.peek(), actual.peekMin());
            assertHeapProperty(actual);
        }

        while (!expected.isEmpty()) {
            assertEquals(expected.poll(), actual.extractMin(metrics));
            assertHeapProperty(actual);
        }

        assertEquals(0, actual.size());
    }

    @Test
    void edgeCasesAndExceptions() {
        DynamicArray array = new DynamicArray();
        MyLinkedList list = new MyLinkedList();
        MinHeap heap = new MinHeap();

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0, metrics));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5, metrics));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0, metrics));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0, metrics));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 5, metrics));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0, metrics));

        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, () -> heap.extractMin(metrics));

        array.add(7, metrics);
        array.add(7, metrics);
        assertTrue(array.contains(7, metrics));
        assertEquals(7, array.remove(0, metrics));
        assertEquals(7, array.get(0, metrics));
    }

    @Test
    void heapSortedOutputWithDuplicates() {
        MinHeap heap = new MinHeap();
        int[] values = {45, 12, 89, 3, 3, 23, 7, 100, 1};

        for (int value : values) {
            heap.insert(value, metrics);
            assertHeapProperty(heap);
        }

        int previous = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            int current = heap.extractMin(metrics);
            assertTrue(current >= previous);
            previous = current;
            assertHeapProperty(heap);
        }
    }

    private void assertHeapProperty(MinHeap heap) {
        int[] data = heap.getHeapArray();
        for (int parent = 0; parent < heap.size(); parent++) {
            int left = 2 * parent + 1;
            int right = left + 1;
            if (left < heap.size()) {
                assertTrue(data[parent] <= data[left]);
            }
            if (right < heap.size()) {
                assertTrue(data[parent] <= data[right]);
            }
        }
    }
}
