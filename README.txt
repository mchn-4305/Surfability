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

SOME OF OUR CURRENT DATA FROM OUR MODEL (different outputs due to random validation set and results)

---- Evaluation for k=14 ----
Test Accuracy:      0.9142710472279261
Test Error:         0.08572895277207393

-- Positive Class (Surfable = 1) --
Precision (Pos):    0.905811623246493
Recall (Pos):       0.925281473899693
F1 Score (Pos):     0.9154430379746836

-- Negative Class (Not Surfable = 0) --
Precision (Neg):    0.9231578947368421
Recall (Neg):       0.9031925849639547
F1 Score (Neg):     0.9130661114003124

Macro F1 Score:     0.9142545746874979

Train Accuracy:     0.9379539086178303
Overall Accuracy:   0.932021773605932
Overall Error:      0.06797822639406803
----------------------------------

---- Evaluation for k=18 ----
Test Accuracy:      0.9132443531827515
Test Error:         0.08675564681724846

-- Positive Class (Surfable = 1) --
Precision (Pos):    0.9076819407008087
Recall (Pos):       0.9203963102152375
F1 Score (Pos):     0.9139949109414758

-- Negative Class (Not Surfable = 0) --
Precision (Neg):    0.9189847009735744
Recall (Neg):       0.906067877956805
F1 Score (Neg):     0.9124805800103573

Macro F1 Score:     0.9132377454759165

Train Accuracy:     0.9328072282266827
Overall Accuracy:   0.9279070764219279
Overall Error:      0.07209292357807207
----------------------------------

---- Evaluation for k=14 ----
Test Accuracy:      0.9106776180698152
Test Error:         0.08932238193018482

-- Positive Class (Surfable = 1) --
Precision (Pos):    0.9026081214922417
Recall (Pos):       0.9233367105707532
F1 Score (Pos):     0.9128547579298831

-- Negative Class (Not Surfable = 0) --
Precision (Neg):    0.919360568383659
Recall (Neg):       0.8976760319112036
F1 Score (Neg):     0.9083889083889084

Macro F1 Score:     0.9106218331593958

Train Accuracy:     0.9379539086178303
Overall Accuracy:   0.9311216835969311
Overall Error:      0.06887831640306885
----------------------------------

---- Evaluation for k=10 ----
Test Accuracy:      0.9067419575633128
Test Error:         0.09325804243668723

-- Positive Class (Surfable = 1) --
Precision (Pos):    0.8996688741721854
Recall (Pos):       0.9182156133828996
F1 Score (Pos):     0.9088476333835089

-- Negative Class (Not Surfable = 0) --
Precision (Neg):    0.9143059490084986
Recall (Neg):       0.8949740034662045
F1 Score (Neg):     0.9045366964442108

Macro F1 Score:     0.9066921649138598

Train Accuracy:     0.9416137702293133
Overall Accuracy:   0.9328790021859329
Overall Error:      0.06712099781406711
----------------------------------

---- Evaluation for k=19 ----
Test Accuracy:      0.9117043121149897
Test Error:         0.0882956878850103

-- Positive Class (Surfable = 1) --
Precision (Pos):    0.9136593591905565
Recall (Pos):       0.9124284270798249
F1 Score (Pos):     0.9130434782608696

-- Negative Class (Not Surfable = 0) --
Precision (Neg):    0.9096908648836401
Recall (Neg):       0.9109565217391304
F1 Score (Neg):     0.9103232533889468

Macro F1 Score:     0.9116833658249082

Train Accuracy:     0.9333218962657974
Overall Accuracy:   0.9279070764219279
Overall Error:      0.07209292357807207
----------------------------------
