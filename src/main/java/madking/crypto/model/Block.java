package madking.crypto.model;

import lombok.Getter;
import madking.crypto.utils.Sha256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

@Getter
public class Block {
    private static final Logger logger = LoggerFactory.getLogger(Block.class);

    private Date timestamp;
    private String previousHash;
    private ArrayList<Transaction> transactions;
    private int nonce = 0;

    private String hash;

    public Block(Date timestamp, String previousHash, ArrayList<Transaction> transactions) {
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactions = transactions;
        try {
            this.hash = calculateHash();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String calculateHash() throws NoSuchAlgorithmException {

        return new Sha256(timestamp
                + previousHash
                + transactions
                + nonce
        ).toString();
    }

    public boolean mineBlock(int difficult) throws NoSuchAlgorithmException {

        while (hash == null || !hash.substring(0, difficult).equals("0".repeat(difficult))) {
            nonce++;
            hash = calculateHash();
        }

        logger.info("Block mined: {}", hash);
        return true;
    }

    public boolean hasValidTransactions() {
        for (Transaction transaction : transactions)
            if (!transaction.isValid())
                return false;
        return true;
    }

}
