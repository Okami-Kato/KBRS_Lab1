package com.epam.encryption;

import com.epam.decryption.KasiskiDecoder;
import com.epam.decryption.impl.CaesarDecoderImpl;
import com.epam.decryption.impl.KasiskiDecoderImpl;
import com.epam.encryption.impl.VigenereEncryptor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EncryptorTest {

    private static final int DEFAULT_ORDER = 0;

    private static final String FIXED_KEY_CSV_FILE_PATH = "summary/fixedKeySummary.csv";

    private static final String FIXED_TEXT_CSV_FILE_PATH = "summary/fixedTextSummary.csv";

    String[] FIXED_KEY_HEADERS = {"length_of_lgram", "text_length", "result"};

    String[] FIXED_TEXT_HEADERS = {"length_of_lgram", "key_length", "result"};

    Encryptor encryptor = new VigenereEncryptor();

    KasiskiDecoder decoder = new KasiskiDecoderImpl(new CaesarDecoderImpl());

    Map<Integer, Map<Integer, List<Boolean>>> fixedKeyStats = new HashMap<>();

    Map<Integer, Map<Integer, List<Boolean>>> fixedTextStats = new HashMap<>();

    private final int minLGramLength = 3;

    private final int maxLGramLength = 8;


    @BeforeAll
    public void setUp() {
        for (int i = minLGramLength; i <= maxLGramLength; i++) {
            fixedKeyStats.put(i, new HashMap<>());
            fixedTextStats.put(i, new HashMap<>());
        }
    }

    @Disabled
    @ParameterizedTest
    @CsvFileSource(resources = "/data/fixedKey.csv", delimiter = '$', numLinesToSkip = 1)
    void encrypt(String text, String key) {
        String encrypted = encryptor.encrypt(text, key.toUpperCase());
        List<String> variants = decoder.decipher(encrypted);
        boolean isSuccess = false;
        for (String variant : variants) {
            if (text.equalsIgnoreCase(variant)) {
                isSuccess = true;
                break;
            }
        }
        assertTrue(isSuccess);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/fixedKey.csv", delimiter = '$', numLinesToSkip = 1)
    @Order(DEFAULT_ORDER)
    public void fixedKeyExperiment(String text, String key) {
        String encrypted = encryptor.encrypt(text, key.toUpperCase());
        for (int i = minLGramLength; i <= maxLGramLength; i++) {
            String deciphered = decoder.decipherByLgramLength(encrypted, i);
            int size = new StringTokenizer(text).countTokens();
            fixedKeyStats.get(i).computeIfAbsent(size, k -> new ArrayList<>());
            fixedKeyStats.get(i).get(size).add(deciphered.equalsIgnoreCase(text));
        }
    }

    @Test
    @Order(DEFAULT_ORDER + 2)
    public void writeFixedKeyResultsToCsv() throws IOException {
        File outputFile = new File(FIXED_KEY_CSV_FILE_PATH);
        outputFile.createNewFile();
        FileWriter out = new FileWriter(outputFile);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.Builder.create().setHeader(FIXED_KEY_HEADERS).build())) {
            for (int i = minLGramLength; i < maxLGramLength; i++) {
                int finalI = i;
                fixedKeyStats.get(i).forEach((textLength, result) -> {
                    try {
                        printer.printRecord(finalI, textLength, result.stream().mapToInt(b -> b ? 1 : 0).average().getAsDouble());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/fixedText.csv", delimiter = '$', numLinesToSkip = 1)
    @Order(DEFAULT_ORDER + 1)
    public void fixedTextExperiment(String text, String key) {
        String encrypted = encryptor.encrypt(text, key.toUpperCase());
        for (int i = minLGramLength; i <= maxLGramLength; i++) {
            String deciphered = decoder.decipherByLgramLength(encrypted, i);
            fixedTextStats.get(i).computeIfAbsent(key.length(), k -> new ArrayList<>());
            fixedTextStats.get(i).get(key.length()).add(deciphered.equalsIgnoreCase(text));
        }
    }

    @Test
    @Order(DEFAULT_ORDER + 3)
    public void writeFixedTextResultsToCsv() throws IOException {
        File outputFile = new File(FIXED_TEXT_CSV_FILE_PATH);
        outputFile.createNewFile();
        FileWriter out = new FileWriter(outputFile);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.Builder.create().setHeader(FIXED_TEXT_HEADERS).build())) {
            for (int i = minLGramLength; i < maxLGramLength; i++) {
                int finalI = i;
                fixedTextStats.get(i).forEach((keyLength, result) -> {
                    try {
                        printer.printRecord(finalI, keyLength, result.stream().mapToInt(b -> b ? 1 : 0).average().getAsDouble());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}