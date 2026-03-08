package com.mindscribe.service;

import org.springframework.stereotype.Service;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    public String predictEmotion(String text) {
        String modelPath = "src/main/resources/model/model.onnx";
        String tokenizerPath = "src/main/resources/model/tokenizer.json";

        try {
            OrtEnvironment env = OrtEnvironment.getEnvironment();

            try (HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance(Paths.get(tokenizerPath));
                 OrtSession session = env.createSession(modelPath, new OrtSession.SessionOptions())) {

                var encoding = tokenizer.encode(text);
                long[] rawIds = encoding.getIds();
                long[] rawMask = encoding.getAttentionMask();

                // Pad or truncate to fixed length 6
                long[] fixedIds = new long[6];
                long[] fixedMask = new long[6];

                for (int i = 0; i < 6; i++) {
                    if (i < rawIds.length) {
                        fixedIds[i] = rawIds[i];
                        fixedMask[i] = rawMask[i];
                    } else {
                        fixedIds[i] = 0;
                        fixedMask[i] = 0;
                    }
                }

                long[][] inputIds = { fixedIds };
                long[][] maskIds = { fixedMask };

                try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputIds);
                     OnnxTensor maskTensor = OnnxTensor.createTensor(env, maskIds)) {

                    Map<String, OnnxTensor> inputs = new HashMap<>();
                    inputs.put("input_ids", inputTensor);
                    inputs.put("attention_mask", maskTensor);

                    try (OrtSession.Result results = session.run(inputs)) {
                        float[][] rawOutput = (float[][]) results.get(0).getValue();
                        float[] probabilities = softmax(rawOutput[0]);

                        int bestClass = 0;
                        for (int i = 1; i < probabilities.length; i++) {
                            if (probabilities[i] > probabilities[bestClass]) {
                                bestClass = i;
                            }
                        }

                        return mapIndexToEmotion(bestClass);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Analysis Error";
        }
    }

    private String mapIndexToEmotion(int index) {
        return switch (index) {
            case 0 -> "Sadness";
            case 1 -> "Joy";
            case 2 -> "Love";
            case 3 -> "Anger";
            case 4 -> "Fear";
            case 5 -> "Surprise";
            default -> "Neutral";
        };
    }

    private float[] softmax(float[] logits) {
        float[] probs = new float[logits.length];
        float sum = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            probs[i] = (float) Math.exp(logits[i]);
            sum += probs[i];
        }
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }
        return probs;
    }
}