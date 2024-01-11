package dauroi.photoeditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

import dauroi.photoeditor.config.DebugOptions;

/**
 * @author Hung Nguyen
 */
@SuppressLint({"DefaultLocale", "TrulyRandom"})
public class SecurityUtils {
    public static class Signature {
        public String date;
        public String signature;
        public String signedPath;
    }



    public static Signature signSimplePath(String path, String method) {
        Signature result = new Signature();
        String date = DateTimeUtils.getCurrentDateTimeGMT();
        String signature = "";
        if (path.endsWith("?")) {
            path = path.concat("date=").concat(date);
        } else {
            path = path.concat("&date=").concat(date);
        }

        try {
            signature = SecurityUtils.createSignature(getSecurityCode(), method, path);
            path = path.concat("&signature=").concat(signature);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        result.date = date;
        result.signature = signature;
        result.signedPath = path;

        return result;
    }

    public static String encodeHmacSHA256(String secret, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] data = sha256_HMAC.doFinal(message.getBytes());
            // String hash = Base64.encodeToString(data, Base64.DEFAULT);
            String hash = Base64.encodeToString(data, Base64.DEFAULT);
            return hash;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String createSignature(String key, String httpMethod, String canonicalizedResource) {
        Map<String, String> headers = new HashMap<String, String>();
        return createSignature(key, httpMethod, canonicalizedResource, headers);
    }

    public static String createSignature(String key, String httpMethod, String canonicalizedResource,
                                         Map<String, String> canonicalizedCustomizedHeaders) {
        StringBuilder headers = new StringBuilder();
        if (canonicalizedCustomizedHeaders != null && canonicalizedCustomizedHeaders.size() > 0) {
            List<String> keys = new ArrayList<String>();
            keys.addAll(canonicalizedCustomizedHeaders.keySet());
            Collections.sort(keys);
            final int size = keys.size();
            for (int idx = 0; idx < size; idx++) {
                headers.append(keys.get(idx).trim().toLowerCase());
                headers.append(":");
                headers.append(canonicalizedCustomizedHeaders.get(keys.get(idx).trim()));
                if (idx < size - 1) {
                    headers.append("\n");
                }
            }
        }

        String strToSign = httpMethod + "\n" + canonicalizedResource;
        if (headers.length() > 0) {
            strToSign = strToSign + "\n" + headers.toString();
        }

        final String signature = encodeHmacSHA256(key, strToSign);

        return signature;
    }

    @SuppressLint("TrulyRandom")
    public static String cipherAES128(String pass, String text, boolean encrypt) throws Exception {
        byte[] keyb = pass.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(keyb);
        SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
        Cipher dcipher = Cipher.getInstance("AES");
        byte[] clearbyte = null;
        if (!encrypt) {
            dcipher.init(Cipher.DECRYPT_MODE, skey);
            clearbyte = dcipher.doFinal(HexUtil.hexToBytes(text));
            return new String(clearbyte);
        } else {
            dcipher.init(Cipher.ENCRYPT_MODE, skey);
            clearbyte = dcipher.doFinal(text.getBytes());
            return HexUtil.bytesToHex(clearbyte);
        }
    }

    public static String getSecurityCode() throws Exception {
        // CkUZDyX7JZZCsY71Z4EMjDW4B+Ys= or YzNlNWM1NDFkYzZkNzljNTEzNjYzNTFhNDU2MDkzNGE=
        return cipherAES128(getMasterSecurity(), getSecurity(), false);
    }

    public static String simpleCipher(final String text, final int pass) {
        int size = text.length();
        char[] arr = text.toCharArray();
        char[] out = new char[arr.length];
        for (int idx = 0; idx < size; idx++) {
            out[idx] = (char) (arr[idx] ^ pass);
        }

        return new String(out);
    }





    public static String sha256s(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


    private static String getSecurity() {
        StringBuffer buf = new StringBuffer();
        if (DebugOptions.ENABLE_FOR_DEV) {
            buf.append("193f10127a83312f1559976486cb3d08226bed6d9948cd90efb2d9057ff01873");
        } else {
            buf.append("3ddc107ce01334e55499928f70953c8adeefa98f961370e5b2fc4cba8c90e68c6c6c2fb9061306699eb23c2768309ad3");
        }

        return buf.toString();
    }

    private static String getMasterSecurity() {
        StringBuffer buf = new StringBuffer();
        buf.append("e4b6def964");
        return buf.toString();
    }
}
