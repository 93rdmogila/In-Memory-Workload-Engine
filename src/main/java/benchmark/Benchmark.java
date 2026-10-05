package benchmark;

import structures.DynamicArray;
import structures.Metrics;
import structures.MinHeap;
import structures.MyLinkedList;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int MEASURED_RUNS = 5;
    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_REPETITIONS = 1;
    private static final int W1_TIMED_REPETITIONS = 1;
    private static final long SEED = 42L;
    private static final Path RESULTS = Path.of("results", "results.csv");

    private Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(RESULTS.getParent());

        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(RESULTS, StandardCharsets.UTF_8))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                int[] data = createData(n);
                runW1(n, data, writer);
                runW2(n, data, writer);
                runW3(n, data, "head", writer);
                runW3(n, data, "middle", writer);
                runW4(n, data, writer);
            }
        }

        System.out.println("Benchmark complete: " + RESULTS);
    }

    private static int[] createData(int n) {
        Random random = new Random(SEED);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt(n * 10 + 1);
        }
        return data;
    }

    private static void runW1(int n, int[] data, PrintWriter writer) {
        final int queries = 10_000;

        int[] indices = new int[queries];
        Random random = new Random(SEED);
        for (int i = 0; i < queries; i++) {
            indices[i] = random.nextInt(n);
        }

        BenchmarkResult arrayResult = measure(() -> {
            DynamicArray array = new DynamicArray();
            Metrics setup = new Metrics();
            for (int value : data) {
                array.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < W1_TIMED_REPETITIONS; repeat++) {
                for (int index : indices) {
                    array.get(index, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / W1_TIMED_REPETITIONS, metrics);
        });

        BenchmarkResult listResult = measure(() -> {
            MyLinkedList list = new MyLinkedList();
            Metrics setup = new Metrics();
            for (int value : data) {
                list.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < W1_TIMED_REPETITIONS; repeat++) {
                for (int index : indices) {
                    list.get(index, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / W1_TIMED_REPETITIONS, metrics);
        });

        save(writer, "W1", "-", "DynamicArray", n, arrayResult);
        save(writer, "W1", "-", "MyLinkedList", n, listResult);
    }

    private static void runW2(int n, int[] data, PrintWriter writer) {
        final int queries = 1_000;
        int[] searchKeys = new int[queries];
        Random random = new Random(SEED);

        for (int i = 0; i < queries / 2; i++) {
            searchKeys[i] = data[random.nextInt(data.length)];
        }
        for (int i = queries / 2; i < queries; i++) {
            searchKeys[i] = -(i + 1);
        }

        BenchmarkResult arrayResult = measure(() -> {
            DynamicArray array = new DynamicArray();
            Metrics setup = new Metrics();
            for (int value : data) {
                array.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < TIMED_REPETITIONS; repeat++) {
                for (int key : searchKeys) {
                    array.contains(key, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / TIMED_REPETITIONS, metrics);
        });

        BenchmarkResult listResult = measure(() -> {
            MyLinkedList list = new MyLinkedList();
            Metrics setup = new Metrics();
            for (int value : data) {
                list.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < TIMED_REPETITIONS; repeat++) {
                for (int key : searchKeys) {
                    list.contains(key, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / TIMED_REPETITIONS, metrics);
        });

        save(writer, "W2", "-", "DynamicArray", n, arrayResult);
        save(writer, "W2", "-", "MyLinkedList", n, listResult);
    }

    private static void runW3(int n, int[] data, String variant, PrintWriter writer) {
        final int operations = 1_000;

        BenchmarkResult arrayResult = measure(() -> {
            DynamicArray array = new DynamicArray();
            Metrics setup = new Metrics();
            for (int value : data) {
                array.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < TIMED_REPETITIONS; repeat++) {
                for (int i = 0; i < operations; i++) {
                    int index = variant.equals("head") ? 0 : array.size() / 2;
                    array.add(index, 999_999_999, metrics);
                    array.remove(index, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / TIMED_REPETITIONS, metrics);
        });

        BenchmarkResult listResult = measure(() -> {
            MyLinkedList list = new MyLinkedList();
            Metrics setup = new Metrics();
            for (int value : data) {
                list.add(value, setup);
            }

            Metrics metrics = new Metrics();
            long start = System.nanoTime();
            for (int repeat = 0; repeat < TIMED_REPETITIONS; repeat++) {
                for (int i = 0; i < operations; i++) {
                    int index = variant.equals("head") ? 0 : list.size() / 2;
                    list.add(index, 999_999_999, metrics);
                    list.remove(index, metrics);
                }
            }
            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6 / TIMED_REPETITIONS, metrics);
        });

        save(writer, "W3", variant, "DynamicArray", n, arrayResult);
        save(writer, "W3", variant, "MyLinkedList", n, listResult);
    }

    private static void runW4(int n, int[] data, PrintWriter writer) {
        BenchmarkResult heapResult = measure(() -> {
            Metrics metrics = new Metrics();
            long start = System.nanoTime();

            MinHeap heap = new MinHeap();
            for (int value : data) {
                heap.insert(value, metrics);
            }

            int previous = Integer.MIN_VALUE;
            for (int i = 0; i < data.length; i++) {
                int current = heap.extractMin(metrics);
                if (current < previous) {
                    throw new AssertionError("extractMin output is not non-decreasing");
                }
                previous = current;
            }

            long elapsed = System.nanoTime() - start;
            return new RunData(elapsed / 1e6, metrics);
        });

        save(writer, "W4", "-", "MinHeap", n, heapResult);
    }

    private static BenchmarkResult measure(BenchmarkTask task) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            task.run();
        }

        RunData[] runs = new RunData[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = task.run();
        }

        Arrays.sort(runs, (a, b) -> Double.compare(a.timeMs, b.timeMs));
        RunData median = runs[MEASURED_RUNS / 2];

        return new BenchmarkResult(
                median.timeMs,
                median.metrics.getSteps(),
                median.metrics.getMoves(),
                median.metrics.getComparisons());
    }

    private static void save(
            PrintWriter writer,
            String workload,
            String variant,
            String structure,
            int n,
            BenchmarkResult result) {

        writer.printf(Locale.ROOT, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload, variant, structure, n,
                result.timeMs, result.steps, result.moves, result.comparisons);

        System.out.printf(Locale.ROOT,
                "%s,%s,%s,n=%d,time=%.3fms,steps=%d,moves=%d,comparisons=%d%n",
                workload, variant, structure, n,
                result.timeMs, result.steps, result.moves, result.comparisons);
    }

    @FunctionalInterface
    private interface BenchmarkTask {
        RunData run();
    }

    private record RunData(
            double timeMs,
            Metrics metrics) {
    }

    private record BenchmarkResult(
            double timeMs,
            long steps,
            long moves,
            long comparisons) {
    }
}
