package com.example.demo.models;

import com.example.demo.models.etsy.EtsyReceipt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EtsyReceiptTest {

    @Test
    public void createTimeToIsoDateTime() {
        EtsyReceipt receipt = new EtsyReceipt();
        receipt.setCreateTimestamp(1677844801L);

        assertEquals("2023-03-03T12:00:01", receipt.getCreateIsoTimeDate());
    }
}
