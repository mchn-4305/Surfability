package edu.surfability.train.models;
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

        for (int k = 1; k <= 80; k++) {
            int correct = 0;

            for (Row r : validation) {
                boolean prediction = predict(k, r);
                if (prediction == r.labelSurfable) {
                    correct++;
                }
            }

            double accuracy = correct / (double) validation.size();
            results.put(k, accuracy);

            System.out.println("k=" + k + " → accuracy=" + accuracy);
        }

        return results;
    }

    public double evaluateTestSet(int k) {
        int correct = 0;

        for (Row r : testing) {
            boolean prediction = predict(k, r);
            if (prediction == r.labelSurfable) {
                correct++;
            }
        }

        double accuracy = correct / (double) testing.size();
        System.out.println("Final Test Accuracy using k=" + k + ": " + accuracy);

        return accuracy;
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


}
