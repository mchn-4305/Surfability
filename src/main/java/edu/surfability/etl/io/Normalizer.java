package edu.surfability.etl.io;

import java.io.*;
import java.util.*;

// Normalizer
// Will keep header and ignore first N columns
public class Normalizer {

    public static void main(String[] args) {
        // File paths relative to the project root
        String inputFile = "data/data_clean.csv";
        String outputFile = "data/normalized_data_clean.csv";

        // Number of columns to skip (e.g., ID or label columns)
        int skipColumns = 3;

        List<String[]> rawRows = new ArrayList<>();
        String[] header = null;

        // STEP 1: Read CSV
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;

            // Read and store header row
            header = br.readLine().split(",");

            // Read remaining rows (data only)
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
        int numCols = cols - skipColumns;

        double[][] data = new double[rows][numCols];

        // STEP 2: Extract numeric data (skip label columns)
        for (int i = 0; i < rows; i++) {
            for (int j = skipColumns; j < cols - 1; j++) {
                try {
                    data[i][j - skipColumns] = Double.parseDouble(rawRows.get(i)[j]);
                } catch (NumberFormatException e) {
                    System.err.println("Non-numeric value at row " + (i + 2) + ", col " + (j + 1));
                    data[i][j - skipColumns] = 0.0;
                }
            }
        }

        // STEP 3: Compute mean and standard deviation for each numeric column
        double[] means = new double[numCols];
        double[] stds = new double[numCols];

        for (int j = 0; j < numCols - 1; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) sum += data[i][j];
            means[j] = sum / rows;

            double variance = 0;
            for (int i = 0; i < rows; i++)
                variance += Math.pow(data[i][j] - means[j], 2);
            stds[j] = Math.sqrt(variance / rows);
        }

        // STEP 4: Apply Z-score normalization
        double[][] normalized = new double[rows][numCols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < numCols - 1; j++) {
                normalized[i][j] = (data[i][j] - means[j]) / stds[j];
            }
        }

        // STEP 5: Write new CSV (including header)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            // Write header first
            for (int j = 0; j < skipColumns; j++) {
                bw.write(header[j]);
                bw.write(",");
            }
            for (int j = skipColumns; j < header.length; j++) {
                bw.write(header[j]);
                if (j < header.length - 1) bw.write(",");
            }
            bw.newLine();

            // Write normalized data
            for (int i = 0; i < rows; i++) {
                // Write skipped columns unchanged
                for (int j = 0; j < skipColumns; j++) {
                    bw.write(rawRows.get(i)[j]);
                    bw.write(",");
                }

                // Write normalized numeric data
                for (int j = 0; j < numCols; j++) {
                    bw.write(String.format("%.5f", normalized[i][j]));
                    if (j < numCols - 1) bw.write(",");
                }
                bw.newLine();
            }

            System.out.println("Normalized data written to: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}



