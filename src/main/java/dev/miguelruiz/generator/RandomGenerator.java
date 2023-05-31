package dev.miguelruiz.generator;

import java.util.Arrays;
import java.util.Random;

public class RandomGenerator {

    public static final double TOLERANCE = 0.001;
    private final int[] numbers;
    private final float[] probabilities;
    private final float[] cumulativeProbabilities;
    private final Random generator;

    public RandomGenerator(int[] numbers, float[] probabilities, Random generator) {

        inputLengthValidation(numbers, probabilities);

        this.numbers = numbers;
        this.probabilities = probabilities;
        this.generator = generator;

        this.cumulativeProbabilities = new float[probabilities.length];

        float cumulativeProbability = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            cumulativeProbabilities[i] = cumulativeProbability;
        }

        // the ABSOLUTE difference (tolerance in either direction) between 1 and the actual probabilities should not be higher that the defined tolerance
        if (Math.abs(1 - cumulativeProbability) > TOLERANCE) {
            throw new IllegalArgumentException("The sum of probabilities must be approximately 1");
        }
    }

    public int[] getNumbers() {
        return numbers;
    }

    public float[] getProbabilities() {
        return probabilities;
    }

    // O(n) -> the complexity of the for loop , it does not scale so well
    // It would take twice as long to run if the size of the input doubled
    public int nextNumber() {

        // the fact that generator.nextFloat() produces uniformly distributed float values between 0.0 and 1.0 and that sum of the probabilities is 1 gave me the idea
        // The premise is that we needed to map the whole range [0.0 - 1.0] to areas whose size kept the proportions given by the probabilities
        //   Explainer:
        //   |---------------|---------| - whole range
        //   |***************|*******+*| - points randomly generated (uniformly across the whole range)
        // (0.0)-----------(0.7)-----(1.0) - whole range space divided into all the probability spaces
        //   |------70%------|---30%---| -> probabilities of 0.7 & 0.3

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

        // todo: we could remove this since it won't be necessary after checking that cummulative probs are ~1.0
        throw new IllegalStateException("Number not found in the ranges. The probabilities should sum up to 1.");
    }

    // O(log n) -> the complexity of the binary search, it scales better!!
    // If you double the size of the input (from n to 2n), an O(log n) algorithm will only need one more unit of time to complete.
    public int nextNumberPro() {

        float generatedNumber = generator.nextFloat();

//        int index = Arrays.binarySearch(cumulativeProbabilities, generatedNumber);
        int index = customBinarySearch(cumulativeProbabilities, generatedNumber);

        // Arrays.binarySearch method returns a "negative insertion point" when the search key is not found in the array or collection.
        // aka the point at which the key would be inserted into the array
        // This allows the method to indicate both whether the key was found (by returning a non-negative value) and where it would go if it wasn't found (by returning the negative insertion point).
        // a hack to combine a boolean (found) and an integer (index) into a single int return value
        // the insertion point is turned negative via a bitwise negation (the operator is ~)
        // The operation is simple: it flips each bit to its opposite. That means each bit that is 0 becomes 1, and each bit that is 1 becomes 0.

        if (index < 0) {
            index = ~index;
            // index = -index - 1 // would also work

            // Explainer:
            // Two's complement is a binary encoding for signed integers that streamlines arithmetic operations in computer science.
            // int a = 10; -->  binary representation in 2's complement (16 digits) of a (10) : 0000000000001010
            // int b = ~a; -->  b will now be -11 - binary representation in 2's complement (16 digits) of a (11) : 1111111111110101
            // the leftmost bit signifies whether the number is positive (0) or negative (1), and the remaining bits are inverted
            // since b will be -11, to turn it back to a (10) we would need to do this operation: a = -b -1
            // In two's complement, positive numbers are represented in the normal binary form and negative numbers are represented as the "complement" of their positive counterpart plus one
            // hence the conversion formula: -index - 1
        }

        return numbers[index];
    }

    private void inputLengthValidation(int[] numbers, float[] probabilities) {
        if (numbers.length != probabilities.length) {
            throw new IllegalArgumentException("Lengths of input array of numbers and probabilities must be the same.");
        }
    }

    //custom binary search
    public static int customBinarySearch(float[] array, float key) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {

            // We calculate the average to find the midpoint
            int mid = (low + high) >>> 1;
            // So, (low + high) >>> 1 is a way to calculate the midpoint between low and high.
            // It's essentially calculating '(low + high) / 2', but with an important difference: it avoids overflow for large low and high.
            // If low and high are both large positive numbers, (low + high) might exceed the maximum positive int value (Integer.MAX_VALUE, which equals 2^31 - 1), causing an overflow.
            // The '>>>' operator handles this gracefully, effectively providing floor division by 2.
            // The use of >>> instead of / for division by 2 is a bit of a micro-optimization, and may not significantly impact performance in modern Java runtimes
            // But it demonstrates a technique for avoiding overflow errors.
            // WHAT IT DOES: it shifts the bits one place (>>> 1) to the right, ergo it is dividing the binary number by 2: 0110 (6) >>> 1: 0011 (3)

            float midVal = array[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid; // key found
            }

        }
        return ~low;  // key not found, returning a negative insertion point
//        return -(low + 1);  // would also work
        // Why do we return low instead of high?
        // Because at the end of the loop, low is the index where the target should be inserted to maintain sorted order
    }
}
