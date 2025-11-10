package edu.surfability.etl.parse;

import edu.surfability.etl.io.CsvUtil;
import edu.surfability.etl.model.NdbcObs;
import org.apache.commons.csv.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

public class NdbcCsvParser {
    public static List<NdbcObs> parse(InputStream in) throws IOException {
        try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVParser p = CSVParser.parse(r, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            List<NdbcObs> out = new ArrayList<>();
            Map<String, Integer> H = p.getHeaderMap();

            String timeHeader = findTimeHeader(H);
            String stationHeader = findHeader(H, List.of("station", "Station"));

            for (CSVRecord rec : p) {
//                Skip unit rows or junk: if time cell doesn't start with a digit, ignore the row
                String tRaw = get(rec, timeHeader);
                if (tRaw == null) {
                    continue;
                }
                String t = tRaw.trim();
                if (t.isEmpty() || !Character.isDigit(t.charAt(0))) {
                    continue; // data labels
                }

                Instant ts;
                try {
                    ts = Instant.parse(t);
                } catch (Exception ex) {
//                    Not ISO-8601? Treat as bad row
                    continue;
                }

                NdbcObs o = new NdbcObs();
                o.time = ts;
                o.stationId = stationHeader == null ? null : get(rec, stationHeader);
                o.wvht_m = num(rec, "wvht");
                o.dpd_s = num(rec, "dpd");
                o.mwd_deg = num(rec, "mwd");
                o.wspd_mps = num(rec, "wspd");
                o.wd_deg = num(rec, "wd");
                o.gst_mps = num(rec, "gst");
                o.atmp_c = num(rec, "atmp");
                o.wtmp_c = num(rec, "wtmp");
                o.bar_hpa = num(rec, "bar");

                out.add(o);
            }
            return out;
        }
    }

    private static String findTimeHeader(Map<String, Integer> H) {
        for (String k : H.keySet()) {
            String kk = k.toLowerCase(Locale.ROOT);
            if (kk.equals("time") || kk.startsWith("time ")) return k; // e.g., "time (UTC)"
        }
//        Fallback: exact match if present
        return H.keySet().stream().filter(k -> k.equalsIgnoreCase("time")).findFirst().orElse(null);
    }

    private static String findHeader(Map<String, Integer> H, List<String> candidates) {
        for (String c : candidates) {
            for (String k : H.keySet()) {
                if (k.equalsIgnoreCase(c)) return k;
            }
        }
        return null;
    }

    private static String get(CSVRecord r, String header) {
        return header == null ? null : r.get(header);
    }

    private static Double num(CSVRecord rec, String key) {
//        Match header case-insensitively
        for (String k : rec.toMap().keySet()) {
            if (k.equalsIgnoreCase(key)) {
                String s = rec.get(k);
                double v = CsvUtil.dOrNa(s);
                return Double.isNaN(v) ? null : v;
            }
        }
        return null;
    }
}