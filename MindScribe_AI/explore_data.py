import pandas as pd

# Load the master file you just created
df = pd.read_csv("data/goemotions_full.csv")

# 1. See how big the dataset is
print(f"Total entries: {len(df)}")

# 2. See the list of all 28 emotions
# In this dataset, emotions are columns from index 9 onwards
emotion_columns = df.columns[9:]
print("\nList of Emotions in the dataset:")
print(list(emotion_columns))

# 3. See an example of text and its emotion
# This shows the text and which emotion column has a '1' (meaning it matches)
sample = df[df['joy'] == 1][['text', 'joy']].head(3)
print("\nExample of 'Joy' entries:")
print(sample)