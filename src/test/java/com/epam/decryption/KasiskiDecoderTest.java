package com.epam.decryption;

import com.epam.decryption.impl.CaesarDecoderImpl;
import com.epam.decryption.impl.KasiskiDecoderImpl;
import com.epam.encryption.Encryptor;
import com.epam.encryption.impl.VigenereEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KasiskiDecoderTest {

    private final String text = """
            They say that good books cannot become old. I can say the same about the books of a unique English
            writer of the 19th century, Charlotte Bronte. She is one of the best representatives of the English
            realism trend. My favourite book by Charlotte Bronte is the novel that brought her fame, "Jane Eyre".
            "Jane Eyre" is an autobiographical novel. The main character is a shy girl, who has, however, a strong
            will and is independent. From her childhood Jane learned that she could rely only on herself. She came
            from a poor family, studied at Lowood institution for poor children, where she had to face many
            difficulties. But she learned to overcome her fears and troubles. She was also able to sympathize with
            other people and give a helping hand in a difficult situation.
            As Charlotte Bronte herself, Jane worked as a governess. Jane fell in love v. th her master, Mr. Rochester.
            Later, she found out that Mr. Rochester was married and she had to leave his house. A new life was not easy
            for her, but she managed to overcome everything and become happy with her beloved, after he had lost wife.
            There are many realistic and romantic details in the novel, Deep feelings of the main characters helped
            them not to lose their ability to share feelings.
            There are many screen versions of the novel and the book is still read with great interest.
            """;

    private final String key = "MOUSE";
    private String encryptedText;

    private Encryptor encryptor;


    @BeforeEach
    void setUp() {
        encryptor = new VigenereEncryptor();
        encryptedText = encryptor.encrypt(text, key);
    }

    @Test
    void decrypt() {
        KasiskiDecoder decoder = new KasiskiDecoderImpl(new CaesarDecoderImpl());
        List<String> variants = decoder.decipher(encryptedText);

        boolean success = false;
        for (String variant : variants) {
            if (variant.equalsIgnoreCase(text)) {
                success = true;
                break;
            }
        }
        assertTrue(success);
    }
}