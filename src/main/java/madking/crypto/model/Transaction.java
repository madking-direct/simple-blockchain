package madking.crypto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import madking.crypto.utils.Ecdsa;
import madking.crypto.utils.KeyGen;
import madking.crypto.utils.Sha256;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Getter
public class Transaction {

    private Date timestamp = new Date();
    private String fromAddress;
    private String toAddress;
    private double amount;

    private String signature;

    public Transaction(String fromAddress, String toAddress, double amount) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }

    public String calculateHash() throws NoSuchAlgorithmException {
        return new Sha256(timestamp
                + fromAddress
                + toAddress
                + amount
        ).toString();
    }

    public void signTransaction(PrivateKey privateKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        signature = Ecdsa.sign(calculateHash(), privateKey);
    }

    @JsonIgnore
    public boolean isValid() {

        if (this.fromAddress == null)
            return true;

        if (this.signature == null || this.signature.length() == 0)
            try {
                throw new SignatureException("No signature found in this transaction");
            } catch (SignatureException e) {
                e.printStackTrace();
                return false;
            }

        try {
            PublicKey publicKey = KeyGen.getPublicKey(this.fromAddress);
            return Ecdsa.verify(calculateHash(), this.signature, publicKey);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

}
