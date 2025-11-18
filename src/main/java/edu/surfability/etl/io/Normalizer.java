package edu.surfability.etl.io;

import java.io.*;
import java.util.*;

// Normalizer
// Will keep header, ignore first N columns, and also ignore the last column
public class Normalizer {

    public static void main(String[] args) {
        // File paths
        String inputFile = "data/data_clean.csv";
        String outputFile = "data/normalized_data_clean.csv";

        // Number of columns to skip at start
        int skipColumns = 3;

        List<String[]> rawRows = new ArrayList<>();
        String[] header = null;

        // STEP 1: Read CSV
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;

            // Header
            header = br.readLine().split(",");

            // Data rows
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                rawRows.add(tokens);
            }

            if (rawRows.isEmpty()) {
                System.err.println("No data found after header.");
                return;
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        int rows = rawRows.size();
        int cols = rawRows.get(0).length;

        // Number of columns to actually normalize
        int numNormalizedCols = cols - skipColumns - 1; // skip first N and last column

        double[][] data = new double[rows][numNormalizedCols];

        // STEP 2: Extract numeric data for normalization
        for (int i = 0; i < rows; i++) {
            for (int j = skipColumns; j < cols - 1; j++) { // up to second-to-last column
                int idx = j - skipColumns;
                if (idx >= numNormalizedCols) break;

                try {
                    data[i][idx] = Double.parseDouble(rawRows.get(i)[j]);
                } catch (NumberFormatException e) {
                    System.err.println("Non-numeric value at row " + (i + 2) + ", col " + (j + 1));
                    data[i][idx] = 0.0;
                }
            }
        }

        // STEP 3: Compute means and stds
        double[] means = new double[numNormalizedCols];
        double[] stds = new double[numNormalizedCols];

        for (int j = 0; j < numNormalizedCols; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) sum += data[i][j];
            means[j] = sum / rows;

            double variance = 0;
            for (int i = 0; i < rows; i++)
                variance += Math.pow(data[i][j] - means[j], 2);
            stds[j] = Math.sqrt(variance / rows);
        }

        // STEP 4: Apply Z-score normalization
        double[][] normalized = new double[rows][numNormalizedCols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < numNormalizedCols; j++) {
                normalized[i][j] = (data[i][j] - means[j]) / stds[j];
            }
        }

        // STEP 5: Write output CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            // Write header
            for (int j = 0; j < header.length; j++) {
                bw.write(header[j]);
                if (j < header.length - 1) bw.write(",");
            }
            bw.newLine();

            // Write rows
            for (int i = 0; i < rows; i++) {

                // Write first N skipped columns unchanged
                for (int j = 0; j < skipColumns; j++) {
                    bw.write(rawRows.get(i)[j]);
                    bw.write(",");
                }

                // Write normalized middle columns
                for (int j = 0; j < numNormalizedCols; j++) {
                    bw.write(String.format("%.5f", normalized[i][j]));
                    bw.write(",");
                }

                // Write last column unchanged
                bw.write(rawRows.get(i)[cols - 1]);

                bw.newLine();
            }

            System.out.println("Normalized data written to: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
