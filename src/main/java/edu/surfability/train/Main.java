package edu.surfability.train;
import java.io.BufferedReader;
import java.time.Instant;
import java.util.*;
import edu.surfability.train.data.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        ArrayList<Row> all_rows = read_normalized_data("data/normalized_data_clean.csv");
        System.out.println(all_rows.get(0));
        System.out.println(all_rows.get(42));
        // Need to split data into training, validation, and testing
        // (validation/testing data must be time-wise (timestampUtc) after the training data)

        // Need to perform KNN algorithm (multiple times iwht hyperparameter tuning)

        // Need to calculate the final algorithm's accuracy/error
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
