package org.ahv.passwordprotectorback.security;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class PasswordSecurity {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String ALGORITHM = "AES";

    @Value("${private.key}")
    private static String privateKey;

    // Chiffre le haché avec la clé privée
    public static SecretKeySpec generateKey(String userId) throws Exception {
        String keyString = userId + privateKey;
        byte[] key = keyString.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance(HASH_ALGORITHM);
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // Utilise les 16 premiers bytes (128 bits) pour AES
        return new SecretKeySpec(key, ALGORITHM);
    }

    // Chiffre le mot de passe avec la clé générée
    public static String encryptPassword(String password, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptPassword(String encryptedPassword, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decrypted);
    }
}
