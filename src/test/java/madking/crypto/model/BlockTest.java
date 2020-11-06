package madking.crypto.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class BlockTest {

    private Block block = new Block(new Date(), "0", new ArrayList<>());

    @Test
    public void blockchainTest() throws NoSuchAlgorithmException, JsonProcessingException {

        System.out.println(block.calculateHash());

    }
}