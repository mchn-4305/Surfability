package edu.surfability.etl.model;

import java.time.*;

public class NdbcObs {
    public String stationId; // 46028: Cape San Martin (closest to slo)
    public Instant time; // UTC
    public Double wvht_m; // significant wave height (m)
    public Double dpd_s; // dominant period (s)
    public Double mwd_deg; // mean wave direction (deg true)
    public Double wspd_mps; // wind speed (m/s)
    public Double wd_deg; // wind direction (deg true)
    public Double gst_mps; // gust (m/s)
    public Double atmp_c; // air temp (°C)
    public Double wtmp_c; // water temp (°C)
    public Double bar_hpa; // pressure (hPa)
}