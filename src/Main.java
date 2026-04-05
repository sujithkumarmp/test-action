import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> values = parseInput(args);
        if (values.isEmpty()) {
            System.out.println("No valid numbers were provided.");
            return;
        }

        Summary summary = summarize(values);
        List<Integer> movingAverage = movingAverage(values, 3);
        int longestRun = longestIncreasingRun(values);

        printReport(values, summary, movingAverage, longestRun);
    }

    private static List<Integer> parseInput(String[] args) {
        if (args.length == 0) {
            return new ArrayList<>(Arrays.asList(12, 14, 18, 17, 21, 25, 24, 26, 30));
        }

        List<Integer> numbers = new ArrayList<>();
        for (String arg : args) {
            String[] pieces = arg.split(",");
            for (String piece : pieces) {
                String trimmed = piece.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        numbers.add(Integer.parseInt(trimmed));
                    } catch (NumberFormatException ignored) {
                        // Skip invalid tokens to keep parsing resilient.
                    }
                }
            }
        }
        return numbers;
    }

    private static Summary summarize(List<Integer> values) {
        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int min = sorted.get(0);
        int max = sorted.get(sorted.size() - 1);
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double median = computeMedian(sorted);
        int range = max - min;

        return new Summary(min, max, mean, median, range);
    }

    private static double computeMedian(List<Integer> sorted) {
        int n = sorted.size();
        int middle = n / 2;
        if (n % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        }
        return sorted.get(middle);
    }

    private static List<Integer> movingAverage(List<Integer> values, int window) {
        List<Integer> result = new ArrayList<>();
        if (window <= 0 || values.size() < window) {
            return result;
        }

        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);
            if (i >= window) {
                sum -= values.get(i - window);
            }
            if (i >= window - 1) {
                result.add((int) Math.round(sum / (double) window));
            }
        }
        return result;
    }

    private static int longestIncreasingRun(List<Integer> values) {
        if (values.isEmpty()) {
            return 0;
        }

        int best = 1;
        int current = 1;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) > values.get(i - 1)) {
                current++;
                best = Math.max(best, current);
            } else {
                current = 1;
            }
        }
        return best;
    }

    private static void printReport(List<Integer> values, Summary summary, List<Integer> movingAverage, int longestRun) {
        System.out.println("Input values: " + values);
        System.out.println("Count: " + values.size());
        System.out.println("Min/Max: " + summary.min + " / " + summary.max);
        System.out.printf("Mean: %.2f, Median: %.2f%n", summary.mean, summary.median);
        System.out.println("Range: " + summary.range);
        System.out.println("3-point moving average (rounded): " + movingAverage);
        System.out.println("Longest increasing run length: " + longestRun);
    }

    private static class Summary {
        final int min;
        final int max;
        final double mean;
        final double median;
        final int range;

        Summary(int min, int max, double mean, double median, int range) {
            this.min = min;
            this.max = max;
            this.mean = mean;
            this.median = median;
            this.range = range;
        }
    }
}
