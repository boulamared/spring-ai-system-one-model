package com.example.systemone;

import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.question.Choice;
import org.springaicommunity.typesafe.question.Question;
import org.springaicommunity.typesafe.response.SystemOneResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DecisionClassificationService {

    public record ClassificationResult(String label, double confidence, Map<String, Double> probabilities) {}

    private final TypeSafeClient typeSafeClient;

    public DecisionClassificationService(TypeSafeClient typeSafeClient) {
        this.typeSafeClient = typeSafeClient;
    }

    /**
     * General purpose single-choice classification using local System One model (tev1:0.8b).
     * Supports options with or without descriptions.
     *
     * Example:
     * classify("The user needs to identify a fruit.", "Which of these is a fruit?", List.of("carrot", "banana", "broccoli"))
     */
    public ClassificationResult classify(String state, String instructions, List<String> options) {
        var choiceBuilder = Choice.builder().instructions(instructions);
        for (String opt : options) {
            choiceBuilder.option(opt);
        }

        SystemOneResponse response = typeSafeClient.systemOne(state, Map.of("classification", choiceBuilder.build()));
        var choice = response.choice("classification");

        return new ClassificationResult(
                choice.value(),
                choice.confidence(),
                choice.probabilities()
        );
    }

    /**
     * Classification with explicit option descriptions for higher semantic calibration.
     */
    public ClassificationResult classifyWithCriteria(String state, String instructions, Map<String, String> optionDescriptions) {
        var choiceBuilder = Choice.builder().instructions(instructions);
        optionDescriptions.forEach(choiceBuilder::option);

        SystemOneResponse response = typeSafeClient.systemOne(state, Map.of("classification", choiceBuilder.build()));
        var choice = response.choice("classification");

        return new ClassificationResult(
                choice.value(),
                choice.confidence(),
                choice.probabilities()
        );
    }

    /**
     * Multi-criteria decision evaluation running arbitrary questions in a single ~100ms pass.
     */
    public SystemOneResponse decide(String state, Map<String, ? extends Question> questions) {
        return typeSafeClient.systemOne(state, questions);
    }
}
