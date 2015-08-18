package oauth2.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtils {

    public static byte[] digest(String input, Charset charset) {
        return digest(input.getBytes(charset));
    }

    public static byte[] digest(byte[] input) {
        return getMd5MessageDigest().digest(input);
    }

    public static MessageDigest getMd5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available. Fatal (should be in the JDK).");
        }
    }

}
