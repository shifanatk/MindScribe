from flask import Flask, request, jsonify
from transformers import pipeline
import json
import os
from datetime import datetime

app = Flask(__name__)

#Load your trained brain
print("MindScribe Engine starting")
classifier = pipeline("text-classification", model="./my_mindscribe_model", tokenizer="./my_mindscribe_model")

#File where history will be saved
HISTORY_FILE = 'mindscribe_history.json'

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    user_text = data.get("text", "")

    results = classifier(user_text, top_k=1)
    prediction = results[0]
    emotion = prediction['label']
    score = float(prediction['score'])

    # --- THE JSON LOGGING PART ---
    new_entry = {
        "timestamp": str(datetime.now()),
        "text": user_text,
        "emotion": emotion,
        "confidence": score
    }

    # Read existing data, add new entry, and save back
    history_data = []
    if os.path.exists(HISTORY_FILE):
        with open(HISTORY_FILE, 'r') as f:
            try:
                history_data = json.load(f)
            except:
                history_data = []

    history_data.append(new_entry)

    with open(HISTORY_FILE, 'w') as f:
        json.dump(history_data, f, indent=4)
        # -----------------------------

    return jsonify({
        "emotion": emotion,
        "confidence": score
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)