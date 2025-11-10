package edu.surfability.etl.io;

import edu.surfability.etl.model.HourlyRow;
import org.apache.commons.csv.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvUtil {
    public static double dOrNa(String s) {
        try { 
            return (s == null || s.isBlank()) ? Double.NaN : Double.parseDouble(s.trim()); 
        } catch (Exception e) { 
            return Double.NaN; 
        } 
    }

    public static void writeHourlyRows(OutputStream os, List<HourlyRow> rows) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             CSVPrinter p = new CSVPrinter(w, CSVFormat.DEFAULT.withHeader(
                     "timestamp_utc","station_id","tide_station",
                     "wvht_m","dpd_s","mwd_deg","wspd_mps","wd_deg","gst_mps",
                     "atmp_c","wtmp_c","bar_hpa","tide_m",
                     "wind_x_mps","wind_y_mps","swell_dir_x","swell_dir_y",
                     "label_surfable"
             ))) {
            for (HourlyRow r : rows) {
                p.printRecord(
                        r.timestampUtc.toString(), r.stationId, r.tideStation,
                        n(r.wvht_m), n(r.dpd_s), n(r.mwd_deg), n(r.wspd_mps), n(r.wd_deg), n(r.gst_mps),
                        n(r.atmp_c), n(r.wtmp_c), n(r.bar_hpa), n(r.tide_m),
                        n(r.wind_x_mps), n(r.wind_y_mps), n(r.swell_dir_x), n(r.swell_dir_y),
                        r.labelSurfable ? 1 : 0
                );
            }
        }
    }

    private static String n(Double v){
        return (v==null || v.isNaN()) ? "" : Double.toString(v);
    }
}