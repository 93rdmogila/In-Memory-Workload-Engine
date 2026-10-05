from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt

CSV_PATH = Path("results/results.csv")
PLOT_DIR = Path("results/plots")


def load_results():
    frame = pd.read_csv(CSV_PATH)
    expected = [
        "workload", "variant", "structure", "n",
        "time_ms", "steps", "moves", "comparisons"
    ]
    if list(frame.columns) != expected:
        raise ValueError(f"Unexpected CSV columns: {list(frame.columns)}")
    return frame


def plot_workload(frame, workload):
    subset = frame[frame["workload"] == workload].copy()
    if subset.empty:
        return

    fig, axes = plt.subplots(2, 1, figsize=(10, 9), sharex=True)

    for (variant, structure), series in subset.groupby(
            ["variant", "structure"], sort=False):
        series = series.sort_values("n")
        label = structure if variant == "-" else f"{structure} ({variant})"
        axes[0].plot(series["n"], series["time_ms"], marker="o", label=label)

    axes[0].set_title(f"{workload}: Time vs n")
    axes[0].set_ylabel("Time (ms)")
    axes[0].set_xscale("log")
    axes[0].set_yscale("log")
    axes[0].grid(True, which="both", alpha=0.3)
    axes[0].legend()

    metrics = ["steps", "moves", "comparisons"]
    for (variant, structure), series in subset.groupby(
            ["variant", "structure"], sort=False):
        series = series.sort_values("n")
        label = structure if variant == "-" else f"{structure} ({variant})"
        for metric in metrics:
            if (series[metric] != 0).any():
                axes[1].plot(
                    series["n"], series[metric],
                    marker="o", label=f"{label}: {metric}"
                )

    axes[1].set_title(f"{workload}: operation counts")
    axes[1].set_xlabel("n")
    axes[1].set_ylabel("Count")
    axes[1].set_xscale("log")
    axes[1].set_yscale("log")
    axes[1].grid(True, which="both", alpha=0.3)
    axes[1].legend(fontsize="small", ncol=2)

    fig.tight_layout()
    fig.savefig(PLOT_DIR / f"{workload.lower()}_overview.png", dpi=200)
    plt.close(fig)


def main():
    PLOT_DIR.mkdir(parents=True, exist_ok=True)
    frame = load_results()
    for workload in ["W1", "W2", "W3", "W4"]:
        plot_workload(frame, workload)
    print("Generated W1-W4 plots.")


if __name__ == "__main__":
    main()
