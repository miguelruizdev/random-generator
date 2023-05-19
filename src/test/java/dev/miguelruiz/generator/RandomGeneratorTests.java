package dev.miguelruiz.generator;

import dev.miguelruiz.generator.utils.RandomGeneratorTester;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class RandomGeneratorTests {

    // Predictable pseudorandom generator using constant seed - required for accurate assertions
    private final Random testGenerator = new Random(12345);

    @Test
    void shouldReturnNumbersRespectingProbabilities() {
        int[] inputNumbers = {7, 5, 9};
        float[] inputProbabilities = {0.1f, 0.8f, 0.1f};

        RandomGenerator randomGenerator = new RandomGenerator(inputNumbers, inputProbabilities, testGenerator);

        Map<Integer, Float> actualProbabilities = RandomGeneratorTester.testNumberGenerator(randomGenerator);

        assertThat(actualProbabilities.get(7)).isEqualTo(0.100787f); // ~10%
        assertThat(actualProbabilities.get(5)).isEqualTo(0.799257f); // ~80%
        assertThat(actualProbabilities.get(9)).isEqualTo(0.099956f); // ~10%
    }

    @Test
    void shouldAggregateProbabilities_whenInputNumbersRepeat() {
        int[] inputNumbers = {7, 5, 9, 1, 1};
        float[] inputProbabilities = {0.2f, 0.3f, 0.3f, 0.1f, 0.1f};

        RandomGenerator randomGenerator = new RandomGenerator(inputNumbers, inputProbabilities, testGenerator);
        Map<Integer, Float> actualProbabilities = RandomGeneratorTester.testNumberGenerator(randomGenerator);

        assertThat(actualProbabilities.get(7)).isEqualTo(0.200922f); // ~20%
        assertThat(actualProbabilities.get(5)).isEqualTo(0.299104f); // ~30%
        assertThat(actualProbabilities.get(9)).isEqualTo(0.299669f); // ~30%
        assertThat(actualProbabilities.get(1)).isEqualTo(0.200305f); // ~20% (~10% + ~10%)
    }

    @Test
    void shouldReturnNumbersRespectingProbabilities_whenUsingNonPredictableRandomGenerator() {
        int[] inputNumbers = {7, 5, 9};
        float[] inputProbabilities = {0.1f, 0.8f, 0.1f};

        Random nonPredictablePseudoRandomGenerator = new Random(); // No seed is provided
        RandomGenerator randomGenerator = new RandomGenerator(inputNumbers, inputProbabilities, nonPredictablePseudoRandomGenerator);

        // Check standard output in terminal for the varying probabilities, always roughly matching ~10% of 7s, ~80% of 5s and ~10% of 9s
        RandomGeneratorTester.testNumberGenerator(randomGenerator);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenInputArraysDifferInLength() {
        // Input arrays differ in length
        int[] inputNumbers = {7, 5, 9, 4};
        float[] inputProbabilities = {0.1f, 0.8f, 0.1f};

        Throwable thrown = catchThrowable(() -> new RandomGenerator(inputNumbers, inputProbabilities, testGenerator));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lengths of input array of numbers and probabilities must be the same.");
    }

    @Test
    void shouldThrowIllegalStateException_whenSumOfProbabilitiesIsNotOne() {
        int[] inputNumbers = {7, 5, 9};
        float[] inputProbabilities = {0.1f, 0.6f, 0.1f}; // Sum is 0.8, not 1.0

        RandomGenerator randomGenerator = new RandomGenerator(inputNumbers, inputProbabilities, testGenerator);

        Throwable thrown = catchThrowable(() -> RandomGeneratorTester.testNumberGenerator(randomGenerator));

        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Number not found in the ranges. The probabilities should sum up to 1.");
    }

}
