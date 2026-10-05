package structures;

public class MinHeap {
    private int[] heap;
    private int size;

    public MinHeap() {
        heap = new int[10];
    }

    public MinHeap(int[] values) {
        if (values == null) {
            throw new NullPointerException("values cannot be null");
        }

        heap = new int[Math.max(10, values.length * 2)];
        System.arraycopy(values, 0, heap, 0, values.length);
        size = values.length;

        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i, new Metrics());
        }
    }

    public int size() {
        return size;
    }

    public void insert(int value, Metrics metrics) {
        ensureCapacity(metrics);
        heap[size] = value;
        size++;
        metrics.addMoves(1);
        siftUp(metrics);
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        return heap[0];
    }

    public int extractMin(Metrics metrics) {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        int result = heap[0];
        metrics.addSteps(1);

        size--;
        if (size > 0) {
            heap[0] = heap[size];
            metrics.addMoves(1);
            siftDown(0, metrics);
        }

        return result;
    }

    public int[] getHeapArray() {
        return heap;
    }

    private void ensureCapacity(Metrics metrics) {
        if (size < heap.length) {
            return;
        }

        int[] newHeap = new int[heap.length * 2];
        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
            metrics.addSteps(1);
            metrics.addMoves(1);
        }
        heap = newHeap;
    }

    private void siftUp(Metrics metrics) {
        int index = size - 1;

        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.addSteps(1);
            metrics.addComparisons(1);

            if (heap[parent] <= heap[index]) {
                break;
            }

            int temp = heap[parent];
            heap[parent] = heap[index];
            heap[index] = temp;
            metrics.addMoves(2);
            index = parent;
        }
    }

    private void siftDown(int index, Metrics metrics) {
        while (true) {
            int left = 2 * index + 1;
            if (left >= size) {
                return;
            }

            int right = left + 1;
            int smallest = left;
            metrics.addSteps(1);

            if (right < size) {
                metrics.addComparisons(1);
                if (heap[right] < heap[left]) {
                    smallest = right;
                }
            }

            metrics.addComparisons(1);
            if (heap[index] <= heap[smallest]) {
                return;
            }

            int temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;
            metrics.addMoves(2);
            index = smallest;
        }
    }
}
