package com.kyeongho.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 *
 * @author 유경호 ykh6242@naver.com
 * @since 2024. 01. 12
 */
public final class EncryptionUtils {

    private static final String ALG = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "biz_yookyengho_enough_long_secret_key";
    private static final String SALT = "20byte_kyeongho_salt";
    private static final String IV = "16byte_bizykh_iv";
    private static final Integer iterationCount = 1000;

    private EncryptionUtils() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * encryptAES256
     * @param text 암호화할 문자열
     * @return AES256 암호화된 BASE64 인코딩 문자열
     */
    public static String encryptAES256(String text) {
        if (isEmpty(text)) {
            throw new IllegalArgumentException("빈 문자열을 암호화하려 하지 마십시오. [text=\"" + text + "\"]");
        }
        byte[] saltBytes = SALT.getBytes(StandardCharsets.UTF_8);
        byte[] bytesIV = IV.getBytes(StandardCharsets.UTF_8);

        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), saltBytes, iterationCount, 256);

            /* KEY + IV setting */
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bytesIV);

            /* Ciphering */
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printBase64Binary(saltBytes)
                    + ";" + DatatypeConverter.printBase64Binary(bytesIV)
                    + ";" + DatatypeConverter.printBase64Binary(encryptedBytes);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * decryptAES256
     * @param encryptedText 복호화할 AES256 암호화된 BASE64 인코딩 문자열
     * @return 복호화된 문자열
     */
    public static String decryptAES256(String encryptedText) {
        String[] splitEncryptedText = encryptedText.split(";");
        if (splitEncryptedText.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 암호문입니다!");
        }

        String base64Salt = splitEncryptedText[0];
        String base64Iv = splitEncryptedText[1];
        String base64CipherText = splitEncryptedText[2];

        byte[] saltBytes = DatatypeConverter.parseBase64Binary(base64Salt);
        byte[] ivBytes = DatatypeConverter.parseBase64Binary(base64Iv);
        byte[] encryptedBytes = DatatypeConverter.parseBase64Binary(base64CipherText);

        try {
            /* KEY + IV setting */
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), saltBytes, iterationCount, 256);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(ivBytes);

            /* Ciphering */
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException("올바르지 않은 암호문입니다!");
        } catch (InvalidAlgorithmParameterException | InvalidKeySpecException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }
}
