package edu.surfability.train;
import java.io.BufferedReader;
import java.time.Instant;
import java.util.*;
import edu.surfability.train.data.*;
import edu.surfability.train.models.KNN;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Reading in normalized clean data
        ArrayList<Row> all_rows = read_normalized_data("data/new_normalized_data_clean.csv");
        System.out.println(all_rows.get(0));
        System.out.println(all_rows.get(42));

        // Splitting into about 70% training, 10% validation, 20% testing
        // With training set data time-wise (timestampUtc) before the validation and testing sets
        ArrayList<Row> train = new ArrayList<>();
        ArrayList<Row> validation = new ArrayList<>();
        ArrayList<Row> test = new ArrayList<>();

        Instant start2024 = Instant.parse("2024-01-01T00:00:00Z");
        ArrayList<Row> rows2024 = new ArrayList<>();

        for (Row r : all_rows) {
            if (r.timestampUtc.isBefore(start2024)) {
                train.add(r);        // All 2022 and 2023 rows → training
            } else {
                rows2024.add(r);     // All 2024 rows → val/test randomization pool
            }
        }

        Collections.shuffle(rows2024, new Random());  // randomize order

        int valCount = (int) Math.ceil(rows2024.size() * 0.30);

        validation.addAll(rows2024.subList(0, valCount));
        test.addAll(rows2024.subList(valCount, rows2024.size()));

        System.out.println("Training rows: " + train.size());
        System.out.println("Validation rows (random): " + validation.size());
        System.out.println("Testing rows (random): " + test.size());

        // Need to perform KNN algorithm (multiple times with hyperparameter tuning)
        KNN model = new KNN(train, validation, test);
        HashMap<Integer, Double> results = model.process();
        int max_k = results.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Need to calculate the final algorithm's accuracy/error
        // and export/visualize results
        model.evaluateTestSet(max_k);
        model.writeAllRowsToCsv("final_labeled_data.csv");
    }

    public static ArrayList<Row> read_normalized_data(String filename) {
        ArrayList<Row> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Split CSV by commas
                String[] parts = line.split(",");

                // Some rows may be incomplete — skip them
                if (parts.length < 16) continue;

                Row r = new Row();

                // ----- Parse Columns -----
                r.timestampUtc = Instant.parse(parts[0]);
                r.stationId = parts[1];
                r.tideStation = parts[2];

                r.wvht_m = parseDouble(parts[3]);
                r.dpd_s = parseDouble(parts[4]);
                r.mwd_deg = parseDouble(parts[5]);
                r.wspd_mps = parseDouble(parts[6]);
                r.wd_deg = parseDouble(parts[7]);
                r.gst_mps = parseDouble(parts[8]);
                r.bar_hpa = parseDouble(parts[9]);
                r.tide_m = parseDouble(parts[10]);

                r.wind_x_mps = parseDouble(parts[11]);
                r.wind_y_mps = parseDouble(parts[12]);
                r.swell_dir_x = parseDouble(parts[13]);
                r.swell_dir_y = parseDouble(parts[14]);

                // Label (0/1 → boolean)
                r.labelSurfable = parts[15].trim().equals("1");

                // Model will fill this later
                r.givenLabel = false;

                rows.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    // Helper for safe parsing
    private static Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null; // or NaN
        }
    }
}
