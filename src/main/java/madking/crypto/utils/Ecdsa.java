package madking.crypto.utils;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class Ecdsa {

    public static String sign(String message, PrivateKey privateKey)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    public static boolean verify(String message, String signature, PublicKey publicKey)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {

        Signature withSignature = Signature.getInstance("SHA256withECDSA");
        withSignature.initVerify(publicKey);
        withSignature.update(message.getBytes(StandardCharsets.UTF_8));

        return withSignature.verify(Base64.getDecoder().decode(signature));
    }

}
