package com.epam;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Util {

    public static final char FIRST_LETTER = 'A';

    public static final char LAST_LETTER = 'Z';

    public static final int ALPHABET_SIZE = LAST_LETTER - FIRST_LETTER + 1;

    /**
     * Cyclically shifts given character by given offset in English alphabet
     * @param character character to shift
     * @param offset offset
     * @return character standing by given offset to the right
     */
    public static char shift(char character, int offset) {
        int originalAlphabetPosition = character - FIRST_LETTER;
        int newAlphabetPosition = (originalAlphabetPosition + offset) % ALPHABET_SIZE;
        return (char) (FIRST_LETTER + newAlphabetPosition);
    }
}
