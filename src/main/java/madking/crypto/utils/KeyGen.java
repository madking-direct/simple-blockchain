package madking.crypto.utils;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.*;

public class KeyGen {

    private final static String EC = "EC";
    private final static String SECP256K1 = "secp256k1";

    private final static BigInteger FieldP_2 = BigInteger.TWO; // constant for scalar operations
    private final static BigInteger FieldP_3 = BigInteger.valueOf(3); // constant for scalar operations

    private final KeyPair keypair;

    public KeyGen() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(EC);
        keyPairGenerator.initialize(
                new ECGenParameterSpec(SECP256K1),
                new SecureRandom()
        );
        keypair = keyPairGenerator.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return keypair.getPublic();
    }

    public static PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(EC);
        EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(toBytes(key));

        return keyFactory.generatePublic(encodedKeySpec);
    }

    public PrivateKey getPrivateKey() {

        return keypair.getPrivate();
    }

    public static PrivateKey gePrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(EC);
        EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(toBytes(key));

        return keyFactory.generatePrivate(encodedKeySpec);
    }

    public static String asHex(PrivateKey privateKey) {

        return asHex(privateKey.getEncoded());
    }

    public static String asHex(PublicKey publicKey) {
        return asHex(publicKey.getEncoded());
    }

    public static String asHex(byte[] bytes) {

        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            stringBuilder.append(String.format("%02x", b));

        return stringBuilder.toString();
    }

    private static byte[] toBytes(String key) {

        int len = key.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(key.charAt(i), 16) << 4)
                    + Character.digit(key.charAt(i + 1), 16));
        }

        return bytes;
    }

    public static PublicKey getPublicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidAlgorithmParameterException, NoSuchProviderException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(EC);

        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;
        ECParameterSpec ecParameterSpec = ecPrivateKey.getParams();
        ECPoint ecPoint = scalmult(ecParameterSpec, ecPrivateKey.getS());

        return keyFactory.generatePublic(
                new ECPublicKeySpec(ecPoint, ecPrivateKey.getParams()));

    }

    private static ECPoint scalmult(ECParameterSpec params, BigInteger kin) {

        ECPoint g = params.getGenerator();
        EllipticCurve curve = params.getCurve();

        ECField field = curve.getField();
        if (!(field instanceof ECFieldFp))
            throw new UnsupportedOperationException(field.getClass().getCanonicalName());

        BigInteger p = ((ECFieldFp) field).getP();
        BigInteger a = curve.getA();

        ECPoint R = ECPoint.POINT_INFINITY;
        // value only valid for curve secp256k1, code taken from https://www.secg.org/sec2-v2.pdf,
        // see "Finally the order n of G and the cofactor are: n = "FF.."

        BigInteger SECP256K1_Q = params.getOrder();
        //BigInteger SECP256K1_Q = new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);

        BigInteger k = kin.mod(SECP256K1_Q); // uses this !
        // BigInteger k = kin.mod(p); // do not use this ! wrong as per comment from President James Moveon Polk
        int length = k.bitLength();
        byte[] binarray = new byte[length];

        for (int i = 0; i <= length - 1; i++) {
            binarray[i] = k.mod(FieldP_2).byteValue();
            k = k.shiftRight(1);
        }

        for (int i = length - 1; i >= 0; i--) {
            R = doublePoint(p, a, R);
            if (binarray[i] == 1) R = addPoint(p, a, R, g);
        }

        return R;
    }

    private static ECPoint doublePoint(final BigInteger p, final BigInteger a, final ECPoint R) {

        if (R.equals(ECPoint.POINT_INFINITY)) return R;

        BigInteger slope = (R.getAffineX().pow(2)).multiply(FieldP_3);
        slope = slope.add(a);
        slope = slope.multiply((R.getAffineY().multiply(FieldP_2)).modInverse(p));
        BigInteger Xout = slope.pow(2).subtract(R.getAffineX().multiply(FieldP_2)).mod(p);
        BigInteger Yout = (R.getAffineY().negate()).add(slope.multiply(R.getAffineX().subtract(Xout))).mod(p);

        return new ECPoint(Xout, Yout);
    }


    private static ECPoint addPoint(final BigInteger p, final BigInteger a, final ECPoint r, final ECPoint g) {

        if (r.equals(ECPoint.POINT_INFINITY)) return g;

        if (g.equals(ECPoint.POINT_INFINITY)) return r;

        if (r == g || r.equals(g)) return doublePoint(p, a, r);

        BigInteger gX = g.getAffineX();
        BigInteger sY = g.getAffineY();
        BigInteger rX = r.getAffineX();
        BigInteger rY = r.getAffineY();
        BigInteger slope = (rY.subtract(sY)).multiply(rX.subtract(gX).modInverse(p)).mod(p);
        BigInteger Xout = (slope.modPow(FieldP_2, p).subtract(rX)).subtract(gX).mod(p);
        BigInteger Yout = sY.negate().mod(p);
        Yout = Yout.add(slope.multiply(gX.subtract(Xout))).mod(p);

        return new ECPoint(Xout, Yout);
    }


}
