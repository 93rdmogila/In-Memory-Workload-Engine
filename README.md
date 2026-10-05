# DAA Assignment 2 — Data Structures

Maven project implementing `DynamicArray`, `MyLinkedList`, and `MinHeap` from scratch for the In-Memory Workload Engine.

## Requirements

- Java 21
- Maven 3.9+
- Python 3 with `pandas` and `matplotlib` for plot generation

The data structures store primitive `int` values and do not use `ArrayList`, `LinkedList`, or `PriorityQueue` internally.

## Project layout

```text
src/main/java/structures/
    DynamicArray.java
    MyLinkedList.java
    MinHeap.java
    Metrics.java
src/main/java/benchmark/
    Benchmark.java
src/test/java/
    DataStructuresTest.java
results/
    results.csv
    plots/
generate_plots.py
REPORT.md
pom.xml
```

## Build

```bash
mvn clean test
```

This compiles the project and runs the JUnit 5 test suite.

## Benchmark

```bash
mvn -q package
java -cp target/classes benchmark.Benchmark
```

The benchmark uses seed `42`, sizes `100, 1000, 10000, 100000`, three warm-up runs and five measured runs. The median measured time is written to `results/results.csv`.

W4 validates that all `extractMin()` results are non-decreasing outside the timing calculation.

## Plots

After generating `results/results.csv`:

```bash
python generate_plots.py
```

Four PNG charts are written to `results/plots/`, one for each workload.

## Git workflow

The repository is organized around:

- `main` — working release branch
- `feature/array`
- `feature/list`
- `feature/heap`
- `feature/metrics`

Release tag: `v1.0`.

## Important benchmark detail

The CSV decimal separator is always `.` via `Locale.ROOT`, so each data row has exactly eight columns:

`workload,variant,structure,n,time_ms,steps,moves,comparisons`.
