# Assignment 2: In-Memory Workload Engine

**Author:** Amanzhol Yerzhanov  
**Group:** SE-2526  
**Release:** `v1.0`

## 1. Complexity table

`N` is the current number of elements.

| Structure | Operation | Best | Average | Worst | Auxiliary space | Justification |
|---|---|---:|---:|---:|---:|---|
| DynamicArray | `add(x)` | Θ(1) | Θ(1) amortized | Θ(N) | O(N) | Appending is constant when capacity exists; a full-array resize copies N elements. |
| DynamicArray | `add(index,x)` | Θ(1) | Θ(N) | Θ(N) | O(N) | Tail insertion shifts zero elements; internal insertion shifts N-index elements and a resize may copy N elements. |
| DynamicArray | `remove(index)` | Θ(1) | Θ(N) | Θ(N) | O(1) | Removing the last element needs no shift; earlier removal shifts the suffix. |
| DynamicArray | `get(index)` | Θ(1) | Θ(1) | Θ(1) | O(1) | Direct array indexing. |
| DynamicArray | `contains(x)` | Θ(1) | Θ(N) | Θ(N) | O(1) | The first cell can match; otherwise the scan may inspect all N cells. |
| MyLinkedList | `add(x)` | Θ(1) | Θ(1) | Θ(1) | O(1) | Tail pointer makes append constant time. |
| MyLinkedList | `add(index,x)` | Θ(1) | Θ(N) | Θ(N) | O(1) | Head/tail insertion is constant; locating an internal node is linear. |
| MyLinkedList | `remove(index)` | Θ(1) | Θ(N) | Θ(N) | O(1) | Removing a boundary node is constant; internal removal requires traversal. |
| MyLinkedList | `get(index)` | Θ(1) | Θ(N) | Θ(N) | O(1) | Sequential traversal is required to locate the node. |
| MyLinkedList | `contains(x)` | Θ(1) | Θ(N) | Θ(N) | O(1) | The first node may match; otherwise all nodes can be inspected. |
| MinHeap | `insert(x)` | Θ(1) | Θ(1) expected | Θ(log N) | O(N) | If the new key is already ≥ its parent, bubble-up stops immediately. For random keys, expected bubble-up distance is constant; a strictly increasing height path gives the worst case. |
| MinHeap | `peekMin()` | Θ(1) | Θ(1) | Θ(1) | O(1) | The minimum is stored at index 0. |
| MinHeap | `extractMin()` | Θ(1) | Θ(log N) | Θ(log N) | O(1) | If the replacement already satisfies the root relation, sift-down stops immediately; otherwise it can travel the heap height. |

**Space note.** For a single `add(x)` on `DynamicArray`, the auxiliary space is **O(N) in the resize case**, because a new 2N array is temporarily allocated. The overall backing storage is O(N). The linked-list operation itself uses O(1) extra references, while the structure stores O(N) nodes.

## 2. Loop-invariant proof: DynamicArray `contains`

The relevant loop is:

```java
for (int i = 0; i < size; i++) {
    metrics.addSteps(1);
    metrics.addComparisons(1);
    if (array[i] == value) {
        return true;
    }
}
return false;
```

### Invariant

Immediately before every iteration with index `i`, the elements at indices `0 ... i-1` have all been compared with `value` and none of them equals `value`.

### Initialization

Before the first iteration, `i = 0`. The range `0 ... i-1` is empty, so the invariant is trivially true.

### Maintenance

During an iteration, `array[i]` is compared with `value`. If they are equal, the method returns `true`, which is correct because a matching element has been found. If they are not equal, then after incrementing `i`, every element in `0 ... i-1` has been checked and none equals `value`. Therefore the invariant remains true.

### Termination

If the loop terminates normally, then `i == size`. By the invariant, every element in `0 ... size-1` was checked and none equals `value`. Returning `false` is therefore correct.

### Conclusion

The invariant proves that `contains` returns `true` exactly when the target occurs in the array.

## 3. Loop-invariant proof: MinHeap `siftDown`

The loop repeatedly selects the smaller child and swaps the current element with it when the heap property is violated.

### Invariant

At the beginning of every `siftDown` iteration, every node outside the subtree rooted at `index` already satisfies the min-heap property, and every node below the current `index` that has already been passed remains a valid heap.

### Initialization

At the start of `extractMin`, the last element is moved to the root. Every subtree below the root was already a valid heap before the extraction, because the structure was a heap before the operation. Thus only the root-to-leaf path may violate the heap property.

### Maintenance

The algorithm chooses the smaller child. If the current value is no greater than that child, it is no greater than either child, so the current subtree is a heap and the loop terminates. Otherwise, swapping with the smaller child restores the parent relation at the old position. The only possible violation moves to the child's position, so the same invariant holds for the next iteration.

### Termination

The loop stops either when the current node has no children or when the current value is no greater than its smallest child. In either case, the affected subtree satisfies the min-heap property.

### Conclusion

Since all unaffected subtrees were already heaps and `siftDown` repairs the only possible violating path, `extractMin` preserves the heap invariant.

## 4. Benchmark methodology

The required sizes are `100`, `1,000`, `10,000`, and `100,000`. The benchmark uses seed `42` and the same generated primitive-int data for structures within a size.

- **W1 — Random Access:** 10,000 random `get(index)` calls.
- **W2 — Search:** 1,000 `contains` queries, exactly half taken from the generated data and half guaranteed absent.
- **W3 — Insert & Remove:** 1,000 insert/remove pairs at index 0 (`head`) and `n/2` (`middle`).
- **W4 — Priority Processing:** n inserts followed by n `extractMin()` calls. The extracted sequence is checked for non-decreasing order outside the timed region's result validation.

Each benchmark case has three warm-up runs and five measured runs. The median measured time is written to `results/results.csv`. The timed workload is repeated five times inside each measured run to make very small workloads less sensitive to timer resolution; reported time is normalized to one workload repetition. Counters are collected by the data-structure methods themselves.

The CSV uses `Locale.ROOT`, so `time_ms` always uses a decimal point and every row has exactly eight columns.

## 5. Expected performance

### W1 — Random access

`DynamicArray.get(i)` is Θ(1), while `MyLinkedList.get(i)` requires traversal and is Θ(N) in the worst case. The array stores primitive integers contiguously, so an indexed read is a direct address calculation. The linked list must follow references through independently allocated nodes.

### W2 — Search

Both structures use linear search and therefore have Θ(N) worst-case complexity. They can nevertheless have different wall-clock times. Array elements are contiguous, allowing cache lines and hardware prefetching to bring nearby integers into the CPU cache. Linked-list traversal performs pointer chasing, so the next node's address is not known until the current node is read.

### W3 — Head and middle updates

At the head, `MyLinkedList` can change a constant number of references, while `DynamicArray` must shift the existing suffix. At the middle, the linked list avoids an array shift but must first traverse to the insertion/removal location. `DynamicArray` can be faster in practice for middle shifts despite moving many integers because contiguous memory has excellent locality.

### W4 — Priority processing

`MinHeap` keeps the minimum at the root. `peekMin()` is Θ(1), while insertion and extraction are bounded by the heap height, Θ(log N) in the worst case. An array-backed heap also has good locality because parent and child positions are computed arithmetically.

## 6. Cache and memory discussion

1. `DynamicArray` stores primitive `int` values consecutively, so one cache line can contain several adjacent elements.
2. Sequential access benefits from spatial locality and hardware prefetching.
3. `MyLinkedList` stores each node separately, so traversing `next` or `prev` is pointer chasing.
4. Pointer chasing can cause cache misses even when the algorithm performs the same number of logical comparisons.
5. A static nested `Node` avoids the extra hidden outer-object reference that a non-static inner class would carry.
6. Linked-list nodes still have object-header and alignment overhead in addition to their integer and references.
7. The dynamic array may temporarily allocate a second array during growth, but after growth its unused capacity is just reserved primitive storage.
8. The heap has compact array storage and does not need one object per element.
9. Therefore equal Big-O bounds do not imply equal wall-clock performance.
10. `MyLinkedList` is a good choice when frequent boundary insertion/removal is more important than indexed access.
11. `DynamicArray` is the better choice for frequent indexed reads and compact sequential data.
12. `MinHeap` is the appropriate choice for priority scheduling where the smallest-priority job must be selected repeatedly.

## 7. Correctness and testing

The JUnit 5 suite checks:

- randomized equivalence of `DynamicArray` against `ArrayList`;
- randomized equivalence of `MyLinkedList` against `LinkedList`;
- randomized equivalence of `MinHeap` against `PriorityQueue`;
- empty structures;
- one-element structures;
- duplicate values;
- first and last positions;
- invalid indices;
- heap property after every insertion and extraction;
- non-decreasing extraction output.

Standard Java collections are used only as test oracles, as permitted by the assignment.

## 8. Limitations

Benchmark time depends on the machine, JVM version, background processes and CPU frequency. The operation counters are intended to expose algorithmic work rather than replace timing. The report therefore uses both counters and wall-clock measurements when discussing performance.
