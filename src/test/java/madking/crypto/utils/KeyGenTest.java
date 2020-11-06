package madking.crypto.utils;

import org.junit.Test;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

public class KeyGenTest {

    @Test
    public void keyGenTests() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, SignatureException, InvalidKeyException {

        KeyGen keyGen = new KeyGen();

        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();

        String privateKeyString = KeyGen.asHex(privateKey);
        String publicKeyString = KeyGen.asHex(publicKey);

        System.out.println("private key : " + privateKeyString);
        System.out.println("public  key : " + publicKeyString);
        System.out.println();

        PrivateKey privateKey1 = KeyGen.gePrivateKey(privateKeyString);
        System.out.println("privateKey1: " + KeyGen.asHex(privateKey1));
        assertTrue("private from String", privateKeyString.equals(KeyGen.asHex(privateKey1)));

        PublicKey publicKey1 = KeyGen.getPublicKey(publicKeyString);
        System.out.println("publicKey1: " + KeyGen.asHex(publicKey1));
        assertTrue("public  from String", publicKeyString.equals(KeyGen.asHex(publicKey1)));

        PublicKey publicKey2 = KeyGen.getPublicKey(privateKey1);
        System.out.println("publicKey2: " + KeyGen.asHex(publicKey2));
        assertTrue("public from private key ", publicKeyString.equals(KeyGen.asHex(publicKey2)));

    }


}