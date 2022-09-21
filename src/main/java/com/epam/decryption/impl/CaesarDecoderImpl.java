package com.epam.decryption.impl;

import com.epam.decryption.CaesarDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.epam.Util.*;

@Slf4j
public class CaesarDecoderImpl implements CaesarDecoder {
    private static final double[] ENGLISH_LETTERS_PROBABILITIES = {0.073, 0.009, 0.030, 0.044, 0.130, 0.028, 0.016, 0.035, 0.074, 0.002, 0.003, 0.035, 0.025, 0.078, 0.074, 0.027, 0.003, 0.077, 0.063, 0.093, 0.027, 0.013, 0.016, 0.005, 0.019, 0.001};

    public String cipher(String message, int offset) {
        StringBuilder result = new StringBuilder();

        for (char character : message.toCharArray()) {
            if (Range.between(FIRST_LETTER, LAST_LETTER).contains(character)) {
                int originalAlphabetPosition = character - FIRST_LETTER;
                int newAlphabetPosition = (originalAlphabetPosition + offset) % ALPHABET_SIZE;
                char newCharacter = (char) (FIRST_LETTER + newAlphabetPosition);
                result.append(newCharacter);
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }

    @Override
    public String decipher(String message, int offset) {
        return cipher(message, ALPHABET_SIZE - (offset % ALPHABET_SIZE));
    }

    @Override
    public String decipher(String message) {
        return decipher(message, probableOffset(chiSquares(message)));
    }

    private double[] chiSquares(String message) {
        double[] expectedLettersFrequencies = expectedLettersFrequencies(message.length());

        double[] chiSquares = new double[ALPHABET_SIZE];

        for (int offset = 0; offset < chiSquares.length; offset++) {
            String decipheredMessage = decipher(message, offset);
            long[] lettersFrequencies = observedLettersFrequencies(decipheredMessage);
            double chiSquare = new ChiSquareTest().chiSquare(expectedLettersFrequencies, lettersFrequencies);
            chiSquares[offset] = chiSquare;
        }

        return chiSquares;
    }

    private long[] observedLettersFrequencies(String message) {
        return IntStream.rangeClosed(FIRST_LETTER, LAST_LETTER)
                .mapToLong(letter -> countLetter((char) letter, message))
                .toArray();
    }

    private long countLetter(char letter, String message) {
        return message.chars()
                .filter(character -> character == letter)
                .count();
    }

    private double[] expectedLettersFrequencies(int messageLength) {
        return Arrays.stream(ENGLISH_LETTERS_PROBABILITIES)
                .map(probability -> probability * messageLength)
                .toArray();
    }

    private int probableOffset(double[] chiSquares) {
        int probableOffset = 0;
        for (int offset = 0; offset < chiSquares.length; offset++) {
            if (chiSquares[offset] < chiSquares[probableOffset]) {
                probableOffset = offset;
            }
        }

        return probableOffset;
    }
}
