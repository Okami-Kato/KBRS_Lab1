package com.epam.decryption.impl;

import com.epam.decryption.KasiskiDecoder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class KasiskiDecoderImpl implements KasiskiDecoder {

    private final CaesarDecoderImpl caesarDecoder;

    @Override
    public List<String> decipher(String message) {
        return IntStream.range(3, 8)
                .boxed()
                .map(i -> getKeywordLength(message, i))
                .filter(length -> length > 1)
                .map(length -> decipherByKeywordLength(message, length))
                .toList();
    }

    @Override
    public String decipherByKeywordLength(String message, int keywordLength) {
        List<String> columns = breakIntoColumns(message, keywordLength);

        List<String> decodedColumns = columns.stream()
                .map(caesarDecoder::decipher)
                .toList();

        return mergeColumns(decodedColumns);
    }

    @Override
    public String decipherByLgramLength(String message, int lGramLength) {
        return decipherByKeywordLength(message, getKeywordLength(message, lGramLength));
    }

    private String mergeColumns(List<String> columns) {
        StringBuilder result = new StringBuilder();
        Integer maxColumnLength = columns.stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Columns array can't be empty"));
        for (int i = 0; i < maxColumnLength; i++){
            for (String column : columns) {
                if (i < column.length()) {
                    result.append(column.charAt(i));
                }
            }
        }
        return result.toString();
    }

    private static List<String> breakIntoColumns(String text, int keywordLength) {
        List<StringBuilder> columns = new ArrayList<>(keywordLength);

        for (int i = 0; i < keywordLength; i++) {
            columns.add(new StringBuilder());
        }

        for (int i = 0; i < text.length(); i++) {
            columns.get(i % keywordLength).append(text.charAt(i));
        }
        return columns.stream()
                .map(StringBuilder::toString)
                .toList();
    }

    /**
     * Finds length of key word used in encryption algorithm
     *
     * @param text   encrypted text
     * @param length l-gram length
     * @return length of key word used in encryption algorithm
     */
    private Integer getKeywordLength(String text, int length) {
        Map<String, Integer> occurrences = new HashMap<>();
        List<Integer> distances = new ArrayList<>();
        for (int i = 0; i + length <= text.length(); i++) {
            String substring = text.substring(i, i + length);
            Integer lastOccurrence = occurrences.get(substring);
            if (lastOccurrence != null) {
                distances.add(i - lastOccurrence);
            }
            occurrences.put(substring, i);
        }
        if (distances.isEmpty()) {
            return 1;
        }
        return gcd(distances);
    }

    private int gcd(List<Integer> numbers) {
        int result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = gcd(numbers.get(i), result);

            if (result == 1) {
                return 1;
            }
        }
        return result;
    }

    private int gcd(int n1, int n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }
}
