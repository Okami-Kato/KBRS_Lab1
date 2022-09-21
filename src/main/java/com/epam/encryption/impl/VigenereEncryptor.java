package com.epam.encryption.impl;

import com.epam.encryption.Encryptor;
import org.apache.commons.lang3.Range;

import static com.epam.Util.*;

public class VigenereEncryptor implements Encryptor {

    /**
     * Encrypts message using Vigenere Cipher
     *
     * @param message message to encrypt
     * @param key  key to use during encryption process
     * @return encrypted message
     */
    @Override
    public String encrypt(String message, String key) {
        String uppercasedText = message.toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uppercasedText.length(); i++) {
            char c = uppercasedText.charAt(i);

            if (!Range.between(FIRST_LETTER, LAST_LETTER).contains(c)) {
                sb.append(c);
            } else {
                sb.append(shift(c, key.charAt(i % key.length()) - FIRST_LETTER));
            }
        }
        return sb.toString();
    }
}
