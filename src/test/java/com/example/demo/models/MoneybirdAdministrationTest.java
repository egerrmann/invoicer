package com.example.demo.models;

import com.example.demo.models.moneybird.MoneybirdAdministration;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneybirdAdministrationTest {
    @Test
    public void getAttributesTest() {
        MoneybirdAdministration administration = new MoneybirdAdministration();

        administration.setId(new BigInteger("123"));
        administration.setName("Test name");

        HashMap<String, Object> attributes = administration.getAttributes();
        assertEquals("123", attributes.get("id").toString());
        assertEquals("Test name", attributes.get("name"));
    }
}
