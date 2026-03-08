import pandas as pd
from datasets import Dataset
from transformers import AutoTokenizer, AutoModelForSequenceClassification, TrainingArguments, Trainer, DataCollatorWithPadding
import torch

# 1. Load your cleaned data
df = pd.read_csv("data/goemotions_final_cleaned.csv").sample(20000, random_state=42) # Start with 8000 rows to test

# 2. Map emotion names to numbers (0-27)
emotions = sorted(df['primary_emotion'].unique().tolist())
label2id = {emotion: i for i, emotion in enumerate(emotions)}
id2label = {i: emotion for i, emotion in enumerate(emotions)}
df['label'] = df['primary_emotion'].map(label2id)

# 3. Convert to HuggingFace Dataset format
dataset = Dataset.from_pandas(df[['text', 'label']])
dataset = dataset.train_test_split(test_size=0.2) # 20% for testing

# 4. Tokenization
tokenizer = AutoTokenizer.from_pretrained("huawei-noah/TinyBERT_General_4L_312D")

def tokenize_function(examples):
    return tokenizer(examples["text"], padding="max_length", truncation=True)

tokenized_datasets = dataset.map(tokenize_function, batched=True)

# 5. Load TinyBERT for Classification
model = AutoModelForSequenceClassification.from_pretrained(
    "huawei-noah/TinyBERT_General_4L_312D", 
    num_labels=28,
    id2label=id2label,
    label2id=label2id
)

# 6. Define Training Settings
training_args = TrainingArguments(
    output_dir="./results",
    eval_strategy="epoch",
    per_device_train_batch_size=16,
    num_train_epochs=5,
    use_cpu=True,
    learning_rate=3e-5,
    weight_decay=0.01,
    logging_steps=10
)

# 7. Start Training
# Create the "packer"
data_collator = DataCollatorWithPadding(tokenizer=tokenizer)

# Start the Trainer
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_datasets["train"],
    eval_dataset=tokenized_datasets["test"],
    data_collator=data_collator, # Ensure this is here!
)

print("Starting training...")
trainer.train()

# 8. Save your trained brain!
model.save_pretrained("./my_mindscribe_model")
tokenizer.save_pretrained("./my_mindscribe_model")
print("Model saved to folder: my_mindscribe_model")