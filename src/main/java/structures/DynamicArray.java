package structures;
public class DynamicArray {
    private int[] array;
    private int size;

    public DynamicArray() {
        this.array = new int[10];
        this.size = 0;
    }

    public DynamicArray(int[] values) {
        if (values == null) {
            throw new NullPointerException("values cannot be null");
        }
        int capacity = Math.max(10, values.length * 2);
        this.array = new int[capacity];
        System.arraycopy(values, 0, this.array, 0, values.length);
        this.size = values.length;
    }

    public int size() {
        return size;
    }

    public void add(int value, Metrics metrics) {
        ensureCapacity(metrics);
        array[size] = value;
        size++;
        metrics.addMoves(1);
    }

    public void add(int index, int value, Metrics metrics) {
        checkPositionIndex(index);
        ensureCapacity(metrics);

        for (int i = size; i > index; i--) {
            array[i] = array[i - 1];
            metrics.addSteps(1);
            metrics.addMoves(1);
        }

        array[index] = value;
        size++;
        metrics.addMoves(1);
    }

    public int remove(int index, Metrics metrics) {
        checkElementIndex(index);

        int removed = array[index];
        metrics.addSteps(1);

        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
            metrics.addSteps(1);
            metrics.addMoves(1);
        }

        size--;
        array[size] = 0;
        return removed;
    }

    public int get(int index, Metrics metrics) {
        checkElementIndex(index);
        metrics.addSteps(1);
        return array[index];
    }

    public boolean contains(int value, Metrics metrics) {
        for (int i = 0; i < size; i++) {
            metrics.addSteps(1);
            metrics.addComparisons(1);
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    private void ensureCapacity(Metrics metrics) {
        if (size < array.length) {
            return;
        }

        int[] newArray = new int[array.length * 2];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
            metrics.addSteps(1);
            metrics.addMoves(1);
        }
        array = newArray;
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
