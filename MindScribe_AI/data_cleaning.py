import pandas as pd

# 1. Load your master dataset
print("Loading data...")
df = pd.read_csv("data/goemotions_full.csv")

# 2. Identify the emotion columns (usually from index 9 onwards)
emotion_cols = list(df.columns[9:])

# 3. Create a function to find which emotion is 'active' (1)
def get_emotion(row):
    for emotion in emotion_cols:
        if row[emotion] == 1:
            return emotion
    return "neutral" # Default if no other emotion is found

print("Mapping 28 emotions into a single column... this might take a moment.")
# Apply the function to every row
df['primary_emotion'] = df.apply(get_emotion, axis=1)

# 4. Keep only what we need for the AI: The Text and the Emotion
cleaned_data = df[['text', 'primary_emotion']]

# 5. Remove 'junk' text (short entries like 'lol' or '.' that confuse the AI)
cleaned_data = cleaned_data[cleaned_data['text'].str.len() > 10]

# 6. Save your final cleaned work
cleaned_data.to_csv("data/goemotions_final_cleaned.csv", index=False)

print("\n--- CLEANING COMPLETE ---")
print(f"Final dataset size: {len(cleaned_data)} rows")
print("Sample of your cleaned data:")
print(cleaned_data.head())