import torch
from transformers import AutoModelForSequenceClassification, AutoTokenizer

# 1. Load your trained model
model_path = "./my_mindscribe_model"
model = AutoModelForSequenceClassification.from_pretrained(model_path)
tokenizer = AutoTokenizer.from_pretrained(model_path)

# 2. Create dummy input (The AI needs to "see" a fake sentence to understand the shape)
text = "I am feeling happy"
inputs = tokenizer(text, return_tensors="pt")

# 3. Export to ONNX
# This creates the 'model.onnx' file Java needs
# In your export_onnx.py script, change the export line to this:
torch.onnx.export(
    model, 
    (inputs['input_ids'], inputs['attention_mask']), 
    "model.onnx",
    input_names=['input_ids', 'attention_mask'],
    output_names=['output'],
    opset_version=11
)

tokenizer.save_pretrained("./onnx_output")

print("Success! Your 'model.onnx' file is now in your folder.")