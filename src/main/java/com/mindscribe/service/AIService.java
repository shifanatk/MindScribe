package com.mindscribe.service;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AIService {

    public void analyzeEmotion(String text) throws Exception {
        String modelPath = "src/main/resources/model/model.onnx";
        String tokenizerPath = "src/main/resources/model/tokenizer.json";

        OrtEnvironment env = OrtEnvironment.getEnvironment();

        try (HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance(Paths.get(tokenizerPath));
             OrtSession session = env.createSession(modelPath, new OrtSession.SessionOptions())) {

            // 1. Encode the text normally
            var encoding = tokenizer.encode(text);
            long[] rawIds = encoding.getIds();
            long[] rawMask = encoding.getAttentionMask();

            // 2. MANUALLY FIX THE SIZE TO 6 (Truncate or Pad)
            long[] fixedIds = new long[6];
            long[] fixedMask = new long[6];

            for (int i = 0; i < 6; i++) {
                if (i < rawIds.length) {
                    fixedIds[i] = rawIds[i];
                    fixedMask[i] = rawMask[i];
                } else {
                    fixedIds[i] = 0; // Padding ID
                    fixedMask[i] = 0; // Mask out the padding
                }
            }

            // 3. Wrap into 2D Tensors
            long[][] inputIds = { fixedIds };
            long[][] maskIds = { fixedMask };

            OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputIds);
            OnnxTensor maskTensor = OnnxTensor.createTensor(env, maskIds);

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("input_ids", inputTensor);
            inputs.put("attention_mask", maskTensor);

            // 4. Run Inference
            try (OrtSession.Result results = session.run(inputs)) {
                float[][] rawOutput = (float[][]) results.get(0).getValue();
                float[] probabilities = softmax(rawOutput[0]);

                // Find the index of the highest percentage
                int bestClass = 0;
                for (int i = 1; i < probabilities.length; i++) {
                    if (probabilities[i] > probabilities[bestClass]) {
                        bestClass = i;
                    }
                }

                System.out.println("\n--- FINAL AI ANALYSIS ---");
                System.out.println("Input: \"" + text + "\"");
                System.out.printf("Highest Emotion (Index %d): %.2f%%\n", bestClass, probabilities[bestClass] * 100);
                System.out.println("--------------------------\n");
            }
        }
    }

    private float[] softmax(float[] logits) {
        float[] probs = new float[logits.length];
        float sum = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            probs[i] = (float) Math.exp(logits[i]);
            sum += probs[i];
        }
        for (int i = 0; i < probs.length; i++) probs[i] /= sum;
        return probs;
    }

    public static void main(String[] args) throws Exception {
        new AIService().analyzeEmotion("I am feeling very anxious today");
    }
}