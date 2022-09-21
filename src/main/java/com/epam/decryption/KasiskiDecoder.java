package com.epam.decryption;

import java.util.List;

public interface KasiskiDecoder {
    List<String> decipher(String message);

    String decipherByKeywordLength(String message, int keywordLength);

    String decipherByLgramLength(String message, int lGramLength);
}
