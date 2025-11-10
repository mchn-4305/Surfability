package edu.surfability.etl.model;

import java.time.*;

public class CoopsObs {
    public String stationId; // e.g., 9412110 (Monterey)
    public Instant time; // UTC
    public Double tide_m; // water level (m) at MLLW datum
}