package dev.miguelruiz.generator;

import java.util.Random;

public class RandomGenerator {

    private final int[] numbers;
    private final float[] probabilities;
    private final Random generator;

    public RandomGenerator(int[] numbers, float[] probabilities, Random generator) {

        inputLengthValidation(numbers, probabilities);

        this.numbers = numbers;
        this.probabilities = probabilities;
        this.generator = generator;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public float[] getProbabilities() {
        return probabilities;
    }

    public int nextNumber() {

        float randomNumber = generator.nextFloat();

        float lowerBound = 0.0f;
        float upperBound = 0.0f;

        for (int i = 0; i < numbers.length; i++) {

            upperBound += probabilities[i];

            if (randomNumber >= lowerBound && randomNumber < upperBound) {
                return numbers[i];
            }

            lowerBound = upperBound;
        }

        throw new IllegalStateException("Number not found in the ranges. The probabilities should sum up to 1.");
    }

    private void inputLengthValidation(int[] numbers, float[] probabilities) {
        if (numbers.length != probabilities.length) {
            throw new IllegalArgumentException("Lengths of input array of numbers and probabilities must be the same.");
        }
    }
}
