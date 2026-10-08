package com.example.systemone;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classify")
public class ClassificationController {

    public record ClassifyRequest(String state, String instructions, List<String> options) {}

    private final DecisionClassificationService classificationService;

    public ClassificationController(DecisionClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    /**
     * POST /api/classify
     *
     * Example payload matching your curl:
     * {
     *   "state": "The user needs to identify a fruit.",
     *   "instructions": "Which of these is a fruit?",
     *   "options": ["carrot", "banana", "broccoli"]
     * }
     */
    @PostMapping
    public DecisionClassificationService.ClassificationResult classify(@RequestBody ClassifyRequest request) {
        return classificationService.classify(
                request.state(),
                request.instructions(),
                request.options()
        );
    }
}
