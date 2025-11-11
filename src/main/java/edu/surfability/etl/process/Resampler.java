package edu.surfability.etl.process;

import edu.surfability.etl.model.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Resampler {
    public static NavigableMap<Instant, NdbcObs> toHourlyNdbc(List<NdbcObs> obs) {
        TreeMap<Instant, List<NdbcObs>> buckets = new TreeMap<>();
        for (NdbcObs o : obs) {
            Instant hour = o.time.truncatedTo(ChronoUnit.HOURS);
            buckets.computeIfAbsent(hour, k -> new ArrayList<>()).add(o);
        }
        NavigableMap<Instant, NdbcObs> out = new TreeMap<>();
        for (var e : buckets.entrySet()) {
            out.put(e.getKey(), meanNdbc(e.getValue()));
        }
        return out;
    }

    public static NavigableMap<Instant, CoopsObs> toHourlyCoops(List<CoopsObs> obs) {
        TreeMap<Instant, List<CoopsObs>> buckets = new TreeMap<>();
        for (CoopsObs o : obs) {
            Instant hour = o.time.truncatedTo(ChronoUnit.HOURS);
            buckets.computeIfAbsent(hour, k -> new ArrayList<>()).add(o);
        }
        NavigableMap<Instant, CoopsObs> out = new TreeMap<>();
        for (var e : buckets.entrySet()) {
            out.put(e.getKey(), meanCoops(e.getValue()));
        }
        return out;
    }

    public static void forwardFill(NavigableMap<Instant, ? extends Object> series, int maxHours) {
//        Generic FF: reflectively fill only Double fields. Small utility for this project.
        if (series.isEmpty()) return;
        try {
            List<Instant> keys = new ArrayList<>(series.keySet());
            for (int i = 1; i < keys.size(); i++) {
                Instant prev = keys.get(i - 1), cur = keys.get(i);
                long gap = Duration.between(prev, cur).toHours();
                if (gap > 1 && gap <= maxHours) {
//                    copy previous object's Double fields into current nulls
                    Object prevObj = series.get(prev);
                    Object curObj  = series.get(cur);
                    for (var f : prevObj.getClass().getFields()) {
                        if (f.getType().equals(Double.class)) {
                            Double pv = (Double) f.get(prevObj);
                            Double cv = (Double) f.get(curObj);
                            if (cv == null && pv != null) {
                                f.set(curObj, pv);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static NdbcObs meanNdbc(List<NdbcObs> ls) {
        NdbcObs m = new NdbcObs();
        m.stationId = ls.get(0).stationId;
        m.time = ls.get(0).time.truncatedTo(ChronoUnit.HOURS);
        m.wvht_m = mean(ls, o -> o.wvht_m);
        m.dpd_s = mean(ls, o -> o.dpd_s);
        m.mwd_deg = meanAngle(ls, o -> o.mwd_deg);
        m.wspd_mps = mean(ls, o -> o.wspd_mps);
        m.wd_deg = meanAngle(ls, o -> o.wd_deg);
        m.gst_mps = mean(ls, o -> o.gst_mps);
        m.atmp_c = mean(ls, o -> o.atmp_c);
        m.wtmp_c = mean(ls, o -> o.wtmp_c);
        m.bar_hpa = mean(ls, o -> o.bar_hpa);
        return m;
    }

    private static CoopsObs meanCoops(List<CoopsObs> ls) {
        CoopsObs m = new CoopsObs();
        m.stationId = ls.get(0).stationId;
        m.time = ls.get(0).time.truncatedTo(ChronoUnit.HOURS);
        m.tide_m = mean(ls, o->o.tide_m);
        return m;
    }

    private interface DGetter<T> {
        Double get(T t);
    }
    private static <T> Double mean(List<T> ls, DGetter<T> g) {
        double s = 0;
        int c = 0;
        for (T t: ls) {
            Double v = g.get(t);
            if (v != null && !v.isNaN()) {
                s += v;
                c++;
            }
        }
        return c == 0 ? null : s / c;
    }
    private static <T> Double meanAngle(List<T> ls, DGetter<T> g){
//        Circular mean for directions in degrees
        double sx = 0, sy = 0;
        int c = 0;
        for (T t: ls) {
            Double v = g.get(t);
            if (v != null && !v.isNaN()) {
                double r = Math.toRadians(v);
                sx += Math.cos(r);
                sy += Math.sin(r);
                c++;
            }
        }
        if (c == 0) {
            return null;
        }
        double ang = Math.atan2(sy / c, sx / c);
        double deg = Math.toDegrees(ang);
        return deg < 0 ? deg + 360 : deg;
    }
}
