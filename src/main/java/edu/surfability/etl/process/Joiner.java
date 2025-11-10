package edu.surfability.etl.process;

import edu.surfability.etl.model.*;
import java.time.*;
import java.util.*;

public class Joiner {
    public static List<HourlyRow> innerJoin(NavigableMap<Instant, NdbcObs> waves,
                                            NavigableMap<Instant, CoopsObs> tides,
                                            String stationId,
                                            String tideStation) {
        List<HourlyRow> out = new ArrayList<>();
        for (var e : waves.entrySet()){
            Instant t = e.getKey();
            CoopsObs co = tides.get(t);
            if (co==null) {
                continue; // inner join
            }
            NdbcObs no = e.getValue();
            HourlyRow r = new HourlyRow();
            r.timestampUtc = t;
            r.stationId = stationId;
            r.tideStation = tideStation;
            r.wvht_m = no.wvht_m;
            r.dpd_s = no.dpd_s;
            r.mwd_deg = no.mwd_deg;
            r.wspd_mps = no.wspd_mps;
            r.wd_deg = no.wd_deg;
            r.gst_mps = no.gst_mps;
            r.atmp_c = no.atmp_c;
            r.wtmp_c = no.wtmp_c;
            r.bar_hpa = no.bar_hpa;
            r.tide_m = co.tide_m;
            out.add(r);
        }
        out.sort(Comparator.comparing(hr -> hr.timestampUtc));
        return out;
    }
}