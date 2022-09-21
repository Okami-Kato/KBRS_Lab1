package com.epam.decryption;

public interface CaesarDecoder {
    String decipher(String message);

    String decipher(String message, int offset);
}
