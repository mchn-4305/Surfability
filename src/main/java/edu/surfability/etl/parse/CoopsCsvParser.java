package edu.surfability.etl.parse;

import edu.surfability.etl.io.CsvUtil;
import edu.surfability.etl.model.CoopsObs;
import org.apache.commons.csv.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CoopsCsvParser {
    public static List<CoopsObs> parse(InputStream in) throws IOException {
        try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVParser p = CSVParser.parse(r, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            List<CoopsObs> out = new ArrayList<>();
            Map<String,Integer> H = p.getHeaderMap();

            for (CSVRecord rec : p) {
                CoopsObs o = new CoopsObs();

//                Try ISO column first (some endpoints return "t")
                String tIso = get(rec, H, "t");
                Instant ts = null;
                if (tIso != null && !tIso.isBlank()) {
                    try {
                        ts = Instant.parse(tIso);
                    } catch (Exception ignore) {
//                        do nothing
                    }
                }

//                Fallback: human "Date Time" column (CO-OPS CSV with time_zone=gmt)
                if (ts == null) {
                    String tHum = get(rec, H, "Date Time");
                    ts = parseCoopsDateTime(tHum); // returns null if not a data row
                }

                if (ts == null) {
//                    Likely the units row (e.g., "UTC") or junk; skip
                    continue;
                }

                o.time = ts;
//                Station ID may exist or not—keep optional
                o.stationId = first(rec, H, List.of("Station ID","station","station_id"));

                String wl = first(rec, H, List.of("Water Level","water_level","v"));
                double v = CsvUtil.dOrNa(wl);
                o.tide_m = Double.isNaN(v) ? null : v;

                out.add(o);
            }
            return out;
        }
    }

    private static Instant parseCoopsDateTime(String s) {
        if (s == null) return null;
        String v = s.trim();
//        Skip obvious non-data (e.g., "UTC", empty, words)
        if (v.isEmpty() || !Character.isDigit(v.charAt(0))) return null;

//        Common CO-OPS formats (GMT):
        DateTimeFormatter[] fs = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
        };
        for (DateTimeFormatter f : fs) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(v, f);
                return ldt.toInstant(ZoneOffset.UTC);
            } catch (Exception ignore) { }
        }
        return null; // unrecognized format: skip row
    }

    private static String first(CSVRecord r, Map<String,Integer> H, List<String> ks){
        for (String k: ks) {
            String v = get(r,H,k);
            if (v!=null) {
                return v;
            }
        }
        return null;
    }
    private static String get(CSVRecord r, Map<String,Integer> H, String key) {
        for (String k : H.keySet()) {
            if (k.equalsIgnoreCase(key)) {
                return r.get(k);
            }
        }
        return null;
    }
}
