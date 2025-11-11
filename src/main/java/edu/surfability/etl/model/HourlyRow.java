package edu.surfability.etl.model;

import java.time.*;

public class HourlyRow {
    public Instant timestampUtc;
    public String stationId; // primary NDBC buoy used
    public String tideStation; // CO-OPS station used

    // Raw / aggregated measurements
    public Double wvht_m, dpd_s, mwd_deg, wspd_mps, wd_deg, gst_mps, atmp_c, wtmp_c, bar_hpa, tide_m;

    // Engineered features
    public Double wind_x_mps, wind_y_mps; // wind vector components
    public Double swell_dir_x, swell_dir_y; // circular encoding of wave direction

    // Label
    public boolean labelSurfable;
}