package madking.crypto.model;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class TransactionTest {

    Transaction transaction = new Transaction("fromAddress", "toAddress", 1);

    @Test
    public void transactionTests() throws NoSuchAlgorithmException {

        System.out.println(transaction.calculateHash());

    }

}