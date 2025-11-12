package edu.surfability.etl;

import edu.surfability.etl.io.*;
import edu.surfability.etl.model.*;
import edu.surfability.etl.parse.*;
import edu.surfability.etl.process.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    /*
     * Requirements:
     * jdk25
     * maven
     * Example usage:
     * mvn clean compile
     * mvn -q exec:java
     */
    public static void main(String[] args) throws Exception {
        Map<String, String> argsMap = parseArgs(args);
        Instant start = Instant.parse(argsMap.getOrDefault("--start", "2024-01-01T00:00:00Z"));
        Instant end = Instant.parse(argsMap.getOrDefault("--end", "2024-03-31T23:00:00Z"));
        String out = argsMap.getOrDefault("--out", "data/raw_surfability_hourly.csv");
        List<String> ndbcStations = argsMap.containsKey("--ndbc") ? List.of(argsMap.get("--ndbc").split(",")) : List.of("46028", "46042"); //Cape San Martin and Monterey
        String tideStation = argsMap.getOrDefault("--tide", "9412110"); // Monterey

//        download + parse NDBC (waves/winds) via ERDDAP CSV
        List<NdbcObs> ndbcAll = new ArrayList<>();
        for (String s : ndbcStations) {
            String url = erddapNdbcUrl(s, start, end);
            try (InputStream in = HttpCsv.get(url)) {
                ndbcAll.addAll(NdbcCsvParser.parse(in));
            }
        }

//        download + parse co-ops (tides) hourly CSV - iterates by month
        List<CoopsObs> tideAll = new ArrayList<>();
        YearMonth from = YearMonth.from(ZonedDateTime.ofInstant(start, ZoneOffset.UTC));
        YearMonth to = YearMonth.from(ZonedDateTime.ofInstant(end, ZoneOffset.UTC));
        YearMonth ym = from;
        while (!ym.isAfter(to)) {
            Instant mStart = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant mEnd = ym.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();
            String url = coopsHourlyUrl(tideStation, mStart, mEnd);
            try (InputStream in = HttpCsv.get(url)) {
                tideAll.addAll(CoopsCsvParser.parse(in));
            }
            ym = ym.plusMonths(1);
        }

//        resample/aggregate to exact top-of-hour, forward-fill small gaps
        NavigableMap<Instant, NdbcObs> ndbcHourly = Resampler.toHourlyNdbc(ndbcAll);
        NavigableMap<Instant, CoopsObs> tideHourly = Resampler.toHourlyCoops(tideAll);
        Resampler.forwardFill(ndbcHourly, 2); // fill ≤2 consecutive missing hours per variable
        Resampler.forwardFill(tideHourly, 6); // tide is smooth; allow up to 6 hours if desired

//        join on timestamp (UTC), prefer waves from first station as primary; keep station id
        String primaryNdbc = ndbcStations.get(0);
        List<HourlyRow> rows = Joiner.innerJoin(ndbcHourly, tideHourly, primaryNdbc, tideStation);
        rows.forEach(DirectionCalculator::augment);

//        label
        for (HourlyRow r : rows) {
            r.labelSurfable = Labeler.ruleBased(
                    r.wvht_m, r.dpd_s, r.mwd_deg,
                    r.wspd_mps, r.wd_deg,
                    r.tide_m
            );
        }

//        write csv
        try (OutputStream os = new FileOutputStream(out)) {
            CsvUtil.writeHourlyRows(os, rows);
        }
    }
    private static String erddapNdbcUrl(String station, Instant start, Instant end) {
        String base = "https://erddap.aoml.noaa.gov/hdb/erddap/tabledap/NDBC_BUOY_1997_present.csv";
        String cols = "station,time,wvht,dpd,mwd,wspd,wd,gst,atmp,wtmp,bar";
        String stationConstraint = "station=" + urlEnc("\"" + station + "\"");
        String timeGe = "time%3E%3D" + urlEnc(start.toString());
        String timeLe = "time%3C%3D" + urlEnc(end.toString());
        return base + "?" + cols + "&" + stationConstraint + "&" + timeGe + "&" + timeLe;
    }

    private static String coopsHourlyUrl(String station, Instant start, Instant end) {
//        CO-OPS hourly verified water level (datum=MLLW), CSV, UTC (gmt)
        String base = "https://api.tidesandcurrents.noaa.gov/api/prod/datagetter";
        String q = String.format(
                "product=water_level&datum=MLLW&station=%s&time_zone=gmt&units=metric&interval=h&format=csv&begin_date=%s&end_date=%s",
                urlEnc(station),
                DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(start),
                DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(end)
        );
        return base + "?" + q;
    }

    private static Map<String,String> parseArgs(String[] args) {
        Map<String,String> m = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String k = args[i];
                String v = (i + 1 < args.length && !args[i + 1].startsWith("--")) ? args[++i] : "";
                m.put(k, v);
            }
        }
        return m;
    }

    private static String urlEnc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}