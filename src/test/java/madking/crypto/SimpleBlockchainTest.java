package madking.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import madking.crypto.model.Block;
import madking.crypto.model.Transaction;
import madking.crypto.utils.KeyGen;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

public class SimpleBlockchainTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleBlockchainTest.class);


    private ObjectMapper objectMapper = new ObjectMapper();
    private ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();


    public SimpleBlockchainTest() throws NoSuchAlgorithmException {
    }


    @Test
    public void simpleBlockchainTests() throws Exception {

        SimpleBlockchain blockchain = new SimpleBlockchain();

        KeyGen john = new KeyGen();
        String johnPublicKey = KeyGen.asHex(john.getPublicKey());

        KeyGen tom = new KeyGen();
        String tomPublicKey = KeyGen.asHex(tom.getPublicKey());

        KeyGen miner = new KeyGen();
        String minerPublicKey = KeyGen.asHex(miner.getPublicKey());

        Transaction t1 = new Transaction(johnPublicKey, tomPublicKey, 100);
        t1.signTransaction(john.getPrivateKey());

        logger.info("Mining block ....");
        blockchain.addTransaction(t1);

        blockchain.minePendingTransactions(minerPublicKey);

        System.out.println("Balance john: " + blockchain.getBalanceForAddress(johnPublicKey));
        System.out.println("Balance tom: " + blockchain.getBalanceForAddress(tomPublicKey));
        System.out.println("Balance miner: " + blockchain.getBalanceForAddress(minerPublicKey));

        blockchain.minePendingTransactions(minerPublicKey);

        System.out.println("Balance john: " + blockchain.getBalanceForAddress(johnPublicKey));
        System.out.println("Balance tom: " + blockchain.getBalanceForAddress(tomPublicKey));
        System.out.println("Balance miner: " + blockchain.getBalanceForAddress(minerPublicKey));

        System.out.println(objectWriter.writeValueAsString(blockchain));

    }

    @Test
    public void blockMining() throws NoSuchAlgorithmException {

        Block block = new Block(new Date(), "0", new ArrayList<Transaction>());
        System.out.println(block.getHash());

        block.mineBlock(3);
        System.out.println(block.getHash());

    }
}