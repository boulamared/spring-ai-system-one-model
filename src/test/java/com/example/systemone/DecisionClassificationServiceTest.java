package com.example.systemone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.response.Answer;
import org.springaicommunity.typesafe.response.ChoiceAnswer;
import org.springaicommunity.typesafe.response.SystemOneResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionClassificationServiceTest {

    @Mock
    private TypeSafeClient typeSafeClient;

    private DecisionClassificationService service;

    @BeforeEach
    void setUp() {
        service = new DecisionClassificationService(typeSafeClient);
    }

    @Test
    void shouldClassifyFruitCorrectly() {
        Map<String, Answer> answers = Map.of(
                "classification", new ChoiceAnswer(
                        "banana",
                        Map.of("carrot", 0.005, "banana", 0.990, "broccoli", 0.005),
                        0.94
                )
        );
        when(typeSafeClient.systemOne(eq("The user needs to identify a fruit."), any()))
                .thenReturn(new SystemOneResponse("tev1:0.8b", answers, null));

        var result = service.classify(
                "The user needs to identify a fruit.",
                "Which of these is a fruit?",
                List.of("carrot", "banana", "broccoli")
        );

        assertThat(result.label()).isEqualTo("banana");
        assertThat(result.confidence()).isEqualTo(0.94);
        assertThat(result.probabilities()).containsEntry("banana", 0.990);
    }
}
