package edu.surfability.train.models;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
import edu.surfability.train.data.*;

public class KNN {
    private ArrayList<Row> training;
    private ArrayList<Row> validation;
    private ArrayList<Row> testing;

    public KNN(ArrayList<Row> training, ArrayList<Row> validation, ArrayList<Row> testing) {
        this.training = training;
        this.validation = validation;
        this.testing = testing;
    }

    public HashMap<Integer, Double> process() {
        HashMap<Integer, Double> results = new HashMap<>(); // K -> accuracy

        // ---- Prepare CSV writer ----
        String csvFile = "knn_validation_accuracy.csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("k,accuracy"); // header row

            for (int k = 1; k <= 20; k++) {
                int correct = 0;

                for (Row r : validation) {
                    boolean prediction = predict(k, r);
                    if (prediction == r.labelSurfable) {
                        correct++;
                    }
                }

                double accuracy = correct / (double) validation.size();
                results.put(k, accuracy);

                // Print to console
                System.out.println("k=" + k + " → accuracy=" + accuracy);

                // Write to CSV
                pw.println(k + "," + accuracy);
            }

            System.out.println("Saved validation accuracy CSV → " + csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }


    public double evaluateTestSet(int k) {

        // --- Confusion matrix (positive class = surfable = true) ---
        int TP = 0, FP = 0, TN = 0, FN = 0;

        // --- Test Accuracy ---
        int testCorrect = 0;
        for (Row r : testing) {
            boolean prediction = predict(k, r);
            boolean actual = r.labelSurfable;

            if (prediction == actual) {
                testCorrect++;
                if (actual) TP++; else TN++;
            } else {
                if (prediction) FP++; else FN++;
            }
        }

        double testAccuracy = testCorrect / (double) testing.size();
        double testError = 1.0 - testAccuracy;

        // --- POSITIVE CLASS METRICS (class = 1 → surfable) ---
        double precisionPos = (TP + FP == 0) ? 0 : TP / (double)(TP + FP);
        double recallPos    = (TP + FN == 0) ? 0 : TP / (double)(TP + FN);
        double f1Pos = (precisionPos + recallPos == 0)
                ? 0
                : 2 * (precisionPos * recallPos) / (precisionPos + recallPos);

        // --- NEGATIVE CLASS METRICS (class = 0 → NOT surfable) ---
        double precisionNeg = (TN + FN == 0) ? 0 : TN / (double)(TN + FN);
        double recallNeg    = (TN + FP == 0) ? 0 : TN / (double)(TN + FP);
        double f1Neg = (precisionNeg + recallNeg == 0)
                ? 0
                : 2 * (precisionNeg * recallNeg) / (precisionNeg + recallNeg);

        // --- MACRO F1 (average of positive and negative F1) ---
        double macroF1 = (f1Pos + f1Neg) / 2.0;

        // --- Training Accuracy ---
        int trainCorrect = 0;
        for (Row r : training) {
            boolean prediction = predict(k, r);
            if (prediction == r.labelSurfable) {
                trainCorrect++;
            }
        }
        double trainAccuracy = trainCorrect / (double) training.size();

        // --- Overall Accuracy ---
        int totalCorrect = testCorrect + trainCorrect;
        int totalSize = testing.size() + training.size();
        double overallAccuracy = totalCorrect / (double) totalSize;
        double overallError = 1.0 - overallAccuracy;

        // --- Print Summary ---
        System.out.println("---- Evaluation for k=" + k + " ----");

        System.out.println("Test Accuracy:      " + testAccuracy);
        System.out.println("Test Error:         " + testError);

        System.out.println("\n-- Positive Class (Surfable = 1) --");
        System.out.println("Precision (Pos):    " + precisionPos);
        System.out.println("Recall (Pos):       " + recallPos);
        System.out.println("F1 Score (Pos):     " + f1Pos);

        System.out.println("\n-- Negative Class (Not Surfable = 0) --");
        System.out.println("Precision (Neg):    " + precisionNeg);
        System.out.println("Recall (Neg):       " + recallNeg);
        System.out.println("F1 Score (Neg):     " + f1Neg);

        System.out.println("\nMacro F1 Score:     " + macroF1);

        System.out.println("\nTrain Accuracy:     " + trainAccuracy);
        System.out.println("Overall Accuracy:   " + overallAccuracy);
        System.out.println("Overall Error:      " + overallError);
        System.out.println("----------------------------------");

        return testAccuracy;
    }






    public boolean predict(int k, Row target) {
        // Compute distance to all training points
        ArrayList<RowDistance> distances = new ArrayList<>();

        for (Row t : training) {
            double d = distance(target, t);
            distances.add(new RowDistance(t, d));
        }

        // Sort by distance ascending
        distances.sort(Comparator.comparingDouble(rd -> rd.distance));

        // Take k nearest neighbors
        int trueCount = 0;
        int falseCount = 0;

        for (int i = 0; i < k; i++) {
            if (distances.get(i).row.labelSurfable)
                trueCount++;
            else
                falseCount++;
        }

        // Majority vote (ties → true)
        boolean prediction = (trueCount >= falseCount);

        target.givenLabel = prediction;
        target.correctlyLabeled = (target.givenLabel == target.labelSurfable);
        return prediction;
    }

    private static class RowDistance {
        Row row;
        double distance;
        RowDistance(Row r, double d) { row = r; distance = d; }
    }


    private double distance(Row a, Row b) {
        double sum = 0.0;
        int count = 0;

        sum += sq(a.wvht_m, b.wvht_m, count++);
        sum += sq(a.dpd_s, b.dpd_s, count++);
        sum += sq(a.mwd_deg, b.mwd_deg, count++);
        sum += sq(a.wspd_mps, b.wspd_mps, count++);
        sum += sq(a.wd_deg, b.wd_deg, count++);
        sum += sq(a.gst_mps, b.gst_mps, count++);
        sum += sq(a.bar_hpa, b.bar_hpa, count++);
        sum += sq(a.tide_m, b.tide_m, count++);

        sum += sq(a.wind_x_mps, b.wind_x_mps, count++);
        sum += sq(a.wind_y_mps, b.wind_y_mps, count++);
        sum += sq(a.swell_dir_x, b.swell_dir_x, count++);
        sum += sq(a.swell_dir_y, b.swell_dir_y, count++);

        return Math.sqrt(sum);
    }

    private double sq(Double x, Double y, int idx) {
        if (x == null || y == null) return 0.0;
        double diff = x - y;
        return diff * diff;
    }

    public void writeAllRowsToCsv(String outputPath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {

            // --- Header (same as your dataset) ---
            pw.println(String.join(",",
                    "timestamp_utc",
                    "station_id",
                    "tide_station",
                    "wvht_m",
                    "dpd_s",
                    "mwd_deg",
                    "wspd_mps",
                    "wd_deg",
                    "gst_mps",
                    "bar_hpa",
                    "tide_m",
                    "wind_x_mps",
                    "wind_y_mps",
                    "swell_dir_x",
                    "swell_dir_y",
                    "label_surfable",
                    "given_label",
                    "correctly_labeled",
                    "split"
            ));

            // --- Helper: write any list with a split tag ---
            java.util.function.BiConsumer<List<Row>, String> writeList = (list, splitName) -> {
                for (Row r : list) {
                    pw.println(String.join(",",
                            safe(r.timestampUtc),
                            safe(r.stationId),
                            safe(r.tideStation),
                            safe(r.wvht_m),
                            safe(r.dpd_s),
                            safe(r.mwd_deg),
                            safe(r.wspd_mps),
                            safe(r.wd_deg),
                            safe(r.gst_mps),
                            safe(r.bar_hpa),
                            safe(r.tide_m),
                            safe(r.wind_x_mps),
                            safe(r.wind_y_mps),
                            safe(r.swell_dir_x),
                            safe(r.swell_dir_y),
                            String.valueOf(r.labelSurfable ? 1 : 0),
                            String.valueOf(r.givenLabel ? 1 : 0),
                            String.valueOf(r.correctlyLabeled ? 1 : 0),
                            splitName
                    ));
                }
            };

            // --- Write all splits ---
            writeList.accept(training, "training");
            writeList.accept(validation, "validation");
            writeList.accept(testing, "testing");

            System.out.println("CSV exported to: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR writing CSV: " + outputPath);
        }
    }

    private String safe(Object o) {
        return (o == null) ? "" : o.toString();
    }




}
