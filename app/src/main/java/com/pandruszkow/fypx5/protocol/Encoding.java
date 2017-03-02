package com.pandruszkow.fypx5.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by piotrek on 24/02/17.
 */
public class Encoding {
    public static String sha256(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return new String(md.digest(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException nsae){
            throw new RuntimeException("Something went very wrong, SHA-256 is not a supported hashing algorithm!");
        }
    }
}
