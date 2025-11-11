package edu.surfability.etl.process;


public class Labeler {
//    Simple, transparent rule: tune to your local break orientation & preferences.
    public static boolean ruleBased(Double wvht_m, Double dpd_s, Double mwd_deg,
                                    Double wspd_mps, Double wd_deg,
                                    Double tide_m){
//        Height: 0.9–2.5 m (~3–8 ft), Period >= 9 s, Wind <= 6 m/s or offshore (tune!), Tide within band
        boolean okHeight = nz(wvht_m) >= 0.9 && nz(wvht_m) <= 2.5;
        boolean okPeriod = nz(dpd_s) >= 9.0;
        boolean okWind = nz(wspd_mps) <= 6.0; // could incorporate whether wind is offshore, onshore, cross-shore but this is simple enough
        boolean okTide = !Double.isNaN(nz(tide_m)) && (nz(tide_m) >= -0.3 && nz(tide_m) <= 1.0);
        int votes = 0;
        votes += okHeight ? 1 : 0;
        votes += okPeriod ? 1 : 0;
        votes += okWind ? 1 : 0;
        votes += okTide ? 1 : 0;
        return votes >= 3; // majority vote of simple criteria
    }

    private static double nz(Double d) {
        return d == null ? Double.NaN : d;
    }
}