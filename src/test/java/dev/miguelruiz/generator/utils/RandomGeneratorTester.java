package dev.miguelruiz.generator.utils;

import dev.miguelruiz.generator.RandomGenerator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record RandomGeneratorTester() {

    public static final int ITERATIONS = 1000000;

    public static Map<Integer, Float> testNumberGenerator(RandomGenerator numberGenerator) {

        Map<Integer, Integer> counters = new LinkedHashMap<>();

        for (int i = 0; i < ITERATIONS; i++) {
            int currentNumber = numberGenerator.nextNumber();
            counters.merge(currentNumber, 1, Integer::sum);
        }

        Map<Integer, Float> probabilities = calculateProbabilities(numberGenerator, counters);
        printProbabilities(counters, probabilities);

        return probabilities;
    }

    private static Map<Integer, Float> calculateProbabilities(RandomGenerator numberGenerator, Map<Integer, Integer> counters) {
        Map<Integer, Float> probabilities = new HashMap<>();

        for (int i = 0; i < numberGenerator.getNumbers().length; i++) {
            float probabilityCurrentNumber = (float) counters.get(numberGenerator.getNumbers()[i]) / ITERATIONS;
            probabilities.put(numberGenerator.getNumbers()[i], probabilityCurrentNumber);
        }
        return probabilities;
    }

    private static void printProbabilities(Map<Integer, Integer> counters, Map<Integer, Float> probabilities) {
        System.out.println("=====================================");
        for (int i = 0; i < counters.keySet().toArray().length; i++) {
            int currentNumber = (int) counters.keySet().toArray()[i];
            System.out.println("Probability of number: " + currentNumber + " - " + probabilities.get(currentNumber));
        }
    }

}
