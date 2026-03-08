from transformers import pipeline

#1. Load the model you just trained
print("Loading your MindScribe AI...")
classifier = pipeline("text-classification", model="./my_mindscribe_model", tokenizer="./my_mindscribe_model")

#2. Test sentences
sentences = [
"I am so grateful for the help you gave me today!",
"I am a bit worried about the exam.",
"This is amazing, I love it!",
"I feel nothing, just a normal day."
]

print("\n--- RESULTS ---")
for text in sentences:
    res = classifier(text, top_k=3)
    print(f"Text: {text}")
    print(f"Emotion: {res[0]['label']} ({round(res[0]['score']*100)}%) \n")