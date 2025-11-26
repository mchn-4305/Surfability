package edu.surfability.train.data;

import java.time.*;

public class Row {
    public Instant timestampUtc;
    public String stationId; // primary NDBC buoy used
    public String tideStation; // CO-OPS station used

    // Raw / aggregated measurements
    public Double wvht_m, dpd_s, mwd_deg, wspd_mps, wd_deg, gst_mps, bar_hpa, tide_m;

    // Engineered features
    public Double wind_x_mps, wind_y_mps; // wind vector components
    public Double swell_dir_x, swell_dir_y; // circular encoding of wave direction

    // Label
    public boolean labelSurfable;
    public boolean givenLabel; // THIS IS WHAT THE MODEL WILL ASSIGN IT

    @Override
    public String toString() {
        return "Row{" +
                "timestampUtc=" + timestampUtc +
                ", stationId='" + stationId + '\'' +
                ", tideStation='" + tideStation + '\'' +
                ", wvht_m=" + wvht_m +
                ", dpd_s=" + dpd_s +
                ", mwd_deg=" + mwd_deg +
                ", wspd_mps=" + wspd_mps +
                ", wd_deg=" + wd_deg +
                ", gst_mps=" + gst_mps +
                ", bar_hpa=" + bar_hpa +
                ", tide_m=" + tide_m +
                ", wind_x_mps=" + wind_x_mps +
                ", wind_y_mps=" + wind_y_mps +
                ", swell_dir_x=" + swell_dir_x +
                ", swell_dir_y=" + swell_dir_y +
                ", labelSurfable=" + labelSurfable +
                ", givenLabel=" + givenLabel +
                '}';
    }
}