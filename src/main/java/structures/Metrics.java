package structures;

public final class Metrics {
    private long steps;
    private long moves;
    private long comparisons;

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }

    public void addSteps(long value) {
        steps += value;
    }

    public void addMoves(long value) {
        moves += value;
    }

    public void addComparisons(long value) {
        comparisons += value;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }
}
