package madking.crypto.utils;

import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

    private static final String SHA3_256 = "SHA3-256";

    private final byte[] bytes;

    public Sha256(String data) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance(SHA3_256);
        bytes = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] getBytes() throws NoSuchAlgorithmException {
        return bytes;
    }

    public String digestAsHex(String value) {
        return new DigestUtils("SHA3-256").digestAsHex(value);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            stringBuilder.append(String.format("%02x", b));

        return stringBuilder.toString();
    }


}
