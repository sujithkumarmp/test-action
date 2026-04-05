import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        writeHtmlReport(values, summary, movingAverage, longestRun);
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

        private static void writeHtmlReport(List<Integer> values, Summary summary, List<Integer> movingAverage, int longestRun) {
                String html = buildHtml(values, summary, movingAverage, longestRun);
                Path output = Path.of("analysis-report.html");
                try {
                        Files.writeString(output, html, StandardCharsets.UTF_8);
                        System.out.println("HTML report created: " + output.toAbsolutePath());
                } catch (Exception ex) {
                        System.out.println("Failed to write HTML report: " + ex.getMessage());
                }
        }

        private static String buildHtml(List<Integer> values, Summary summary, List<Integer> movingAverage, int longestRun) {
                String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String mean = String.format("%.2f", summary.mean);
                String median = String.format("%.2f", summary.median);

            String template = """
                        <!doctype html>
                        <html lang=\"en\">
                        <head>
                            <meta charset=\"UTF-8\" />
                            <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                            <title>Analytics Report</title>
                            <style>
                                :root {
                                    --bg: #f4f7fb;
                                    --card: #ffffff;
                                    --text: #1c2630;
                                    --muted: #5a6a7a;
                                    --accent: #1f8ef1;
                                    --accent-2: #16c79a;
                                    --shadow: 0 12px 30px rgba(20, 40, 80, 0.12);
                                }
                                body {
                                    margin: 0;
                                    font-family: \"Segoe UI\", \"Trebuchet MS\", sans-serif;
                                    background: radial-gradient(circle at top right, #d9ebff, var(--bg) 45%);
                                    color: var(--text);
                                    min-height: 100vh;
                                    display: grid;
                                    place-items: center;
                                    padding: 24px;
                                }
                                .card {
                                    width: min(900px, 95vw);
                                    background: var(--card);
                                    border-radius: 16px;
                                    box-shadow: var(--shadow);
                                    padding: 24px;
                                }
                                h1 {
                                    margin: 0 0 10px;
                                    font-size: 1.8rem;
                                }
                                .sub {
                                    margin-bottom: 20px;
                                    color: var(--muted);
                                }
                                .grid {
                                    display: grid;
                                    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
                                    gap: 12px;
                                    margin-bottom: 16px;
                                }
                                .metric {
                                    background: linear-gradient(135deg, #f7fbff, #eef7ff);
                                    border: 1px solid #d7e8fb;
                                    border-radius: 12px;
                                    padding: 12px;
                                }
                                .metric .label {
                                    color: var(--muted);
                                    font-size: 0.85rem;
                                }
                                .metric .value {
                                    margin-top: 6px;
                                    font-size: 1.25rem;
                                    font-weight: 700;
                                    color: var(--accent);
                                }
                                .list {
                                    margin-top: 8px;
                                    background: #fafcff;
                                    border: 1px solid #e4edf8;
                                    border-radius: 10px;
                                    padding: 12px;
                                    font-family: ui-monospace, \"Cascadia Code\", Consolas, monospace;
                                    overflow-x: auto;
                                }
                                .footer {
                                    margin-top: 20px;
                                    color: var(--muted);
                                    font-size: 0.85rem;
                                }
                                .dot {
                                    display: inline-block;
                                    width: 10px;
                                    height: 10px;
                                    border-radius: 50%;
                                    background: var(--accent-2);
                                    margin-right: 6px;
                                }
                            </style>
                        </head>
                        <body>
                            <section class=\"card\">
                                <h1>Java Analytics Report</h1>
                                <div class=\"sub\"><span class=\"dot\"></span>Generated from Main.java data processing pipeline</div>

                                <div class=\"grid\">
                                    <div class=\"metric\"><div class=\"label\">Count</div><div class=\"value\">{{COUNT}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Min</div><div class=\"value\">{{MIN}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Max</div><div class=\"value\">{{MAX}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Mean</div><div class=\"value\">{{MEAN}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Median</div><div class=\"value\">{{MEDIAN}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Range</div><div class=\"value\">{{RANGE}}</div></div>
                                    <div class=\"metric\"><div class=\"label\">Longest Increasing Run</div><div class=\"value\">{{LONGEST_RUN}}</div></div>
                                </div>

                                <h3>Input Values</h3>
                                <div class=\"list\">{{VALUES}}</div>

                                <h3>3-point Moving Average (Rounded)</h3>
                                <div class=\"list\">{{MOVING_AVG}}</div>

                                <div class=\"footer\">Generated at: {{GENERATED_AT}}</div>
                            </section>
                        </body>
                        </html>
                        """;

                return template
                        .replace("{{COUNT}}", String.valueOf(values.size()))
                        .replace("{{MIN}}", String.valueOf(summary.min))
                        .replace("{{MAX}}", String.valueOf(summary.max))
                        .replace("{{MEAN}}", mean)
                        .replace("{{MEDIAN}}", median)
                        .replace("{{RANGE}}", String.valueOf(summary.range))
                        .replace("{{LONGEST_RUN}}", String.valueOf(longestRun))
                        .replace("{{VALUES}}", escapeHtml(values.toString()))
                        .replace("{{MOVING_AVG}}", escapeHtml(movingAverage.toString()))
                        .replace("{{GENERATED_AT}}", escapeHtml(generatedAt));
        }

        private static String escapeHtml(String text) {
                return text
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
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
