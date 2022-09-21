package com.epam;

import com.epam.encryption.impl.VigenereEncryptor;

public class Main {
    public static void main(String[] args) {
        System.out.println(new VigenereEncryptor().encrypt("Hello world hello world", "MOUSEA"));
    }
}