print("--- Starting Script ---") # Check if the script even starts

from transformers import pipeline
print("Libraries imported!") 

print("Loading TinyBERT... (This may take a minute on first run)")
try:
    classifier = pipeline("sentiment-analysis", model="huawei-noah/TinyBERT_General_4L_312D")
    print("Model loaded successfully!")
    
    test_text = "I am feeling very anxious today"
    result = classifier(test_text)

    print("-" * 20)
    print(f"Input: {test_text}")
    print(f"Result: {result}")
    print("-" * 20)
    
except Exception as e:
    print(f"An error occurred: {e}")

print("--- Script Finished ---")