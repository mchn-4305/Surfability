SURFABILITY:
This project pulls historical surf and weather data from buoys and uses a model to determine
whether a given day is surfable or not. This is based on a rule defined as such:

Surfability = 1 iff 3/4 principle conditions met:
    wave height 0.9–2.5 m (~3–8 ft)
    swell period >= 9 s
    wind <= 6 m/s
    tide -0.3-1m
Surfability = 0 otherwise

CONTEXT:
NOAA: National Oceanic and Atmospheric Administration
NDBC: National Data Buoy Center
CO-OPS: Center for Operational Oceanographic Products and Services
ERDDAP: Environmental Research Division's Data Access Program

WVHT_M: Significant Wave Height (meters)
DPD_S: Dominant Period of waves (seconds)
MWD_DEG: Mean Wave Direction (degrees clockwise from North)
WSPD_MPS: Wind Speed (meters per second)
WD_DEG: Wind Direction (degrees clockwise from North)
GST_MPS: Wind Gust (meters per second)
ATMP_C: Air Temperature (Celsius)
WTMP_C: Water Temperature (Celsius)
BAR_HPA: Barometric Pressure (Hectopascals)
TIDE_M: Hourly Water Level (meters) referenced to MLLW (Mean Lower Low Water)
WIND_[X,Y]_MPS: Wind Vector Components = WSPD_MPS * [cos,sin](WD_DEG)
SWELL_DIR_[X,Y]: Circular encoding of MWD_DEG = [cos,sin](MWD_DEG)
LABEL_SURFABLE: Binary Label (1 = surfable, 0 = not surfable)

ETL (Micah):
insert etl functionality here




