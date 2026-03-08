import pandas as pd
import os

# Create the data directory if it doesn't exist
if not os.path.exists('data'):
    os.makedirs('data')

base_url = "https://storage.googleapis.com/gresearch/goemotions/data/full_dataset/"
files = ["goemotions_1.csv", "goemotions_2.csv", "goemotions_3.csv"]

print("Connecting to Google Research Storage...")
all_frames = []

for f in files:
    print(f"Downloading {f}...")
    df = pd.read_csv(base_url + f)
    all_frames.append(df)

# Merge them
print("Merging datasets...")
full_df = pd.concat(all_frames)

# Save the master copy inside the 'data' folder
full_df.to_csv("data/goemotions_full.csv", index=False)
print(f"Done! Created data/goemotions_full.csv with {len(full_df)} rows.")