package madking.crypto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import madking.crypto.model.Block;
import madking.crypto.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

@Getter
public class SimpleBlockchain {
    private static final Logger logger = LoggerFactory.getLogger(SimpleBlockchain.class);

    private final ArrayList<Block> blockchain = new ArrayList<>();

    private ArrayList<Transaction> pendingTransactions = new ArrayList<>();

    private int difficult = 2;
    private double rewardAmount = 8;

    public SimpleBlockchain() throws NoSuchAlgorithmException {
        blockchain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() throws NoSuchAlgorithmException {
        Block block = new Block(new Date(), "0", new ArrayList<>());
        return block;
    }

    public void minePendingTransactions(String rewardAddress) throws NoSuchAlgorithmException {

        Block block = new Block(new Date(), getLastBlock().getHash(), pendingTransactions);
        if (block.mineBlock(difficult)) {
            blockchain.add(block);
            pendingTransactions = new ArrayList<>(){{
                add(new Transaction(null, rewardAddress, rewardAmount));
            }};
        }
    }


    public void addTransaction(Transaction transaction) throws Exception {

        if (transaction.getFromAddress() == null || transaction.getToAddress() == null)
            throw new Exception("Transaction must include a From and To addresses");

        if (!transaction.isValid())
            throw new Exception("Cannot add invalid transaction to chain");

        pendingTransactions.add(transaction);
    }

    @JsonIgnore
    public Block getLastBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    @JsonIgnore
    public Double getBalanceForAddress(String address) {
        double balance = 0.0;
        for (Block block : blockchain) {
            for (Transaction transaction : block.getTransactions()) {

                if (address.equals(transaction.getToAddress()))
                    balance += transaction.getAmount();

                if (address.equals(transaction.getFromAddress()))
                    balance -= transaction.getAmount();
            }
        }
        return balance;
    }

    @JsonIgnore
    public boolean isChainValid() throws NoSuchAlgorithmException {

        for (int i = 1; i < blockchain.size(); i++) {
            Block thisBlock = blockchain.get(i);

            if (!thisBlock.hasValidTransactions())
                return false;

            //Checked the block hash is correctly calculated
            if (!thisBlock.getHash().equals(thisBlock.calculateHash()))
                return false;

            Block prevBlock = blockchain.get(i - 1);
            //Checked the block hash is same as previous block
            if (!thisBlock.getPreviousHash().equals(prevBlock.getHash()))
                return false;
        }
        return true;
    }


}
