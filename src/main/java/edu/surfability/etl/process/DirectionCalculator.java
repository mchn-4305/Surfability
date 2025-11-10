package edu.surfability.etl.process;

import edu.surfability.etl.model.HourlyRow;

public class DirectionCalculator {
    public static void augment(HourlyRow r){
//        wind vector components (meteorological -> we just encode heading)
        if (r.wspd_mps != null && r.wd_deg != null){
            double th = Math.toRadians(r.wd_deg);
            r.wind_x_mps = r.wspd_mps * Math.cos(th);
            r.wind_y_mps = r.wspd_mps * Math.sin(th);
        }
        if (r.mwd_deg != null){
            double ths = Math.toRadians(r.mwd_deg);
            r.swell_dir_x = Math.cos(ths);
            r.swell_dir_y = Math.sin(ths);
        }
    }
}