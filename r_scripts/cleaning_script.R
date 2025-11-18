library(tidyverse)
raw_data <- read_csv("../data/raw_surfability_hourly.csv")

# Check original size
dim(raw_data)

# Drop temperature column
data_clean <- raw_data %>%
  select(-atmp_c, -wtmp_c)

# Drop all rows that have any NA values
data_clean <- data_clean %>%
  drop_na()

# Check the size (rows × columns)
dim(data_clean)

# print a preview
head(data_clean)

write_csv(data_clean, "../data/data_clean.csv")
