package com.example.demo.models;

import com.example.demo.models.moneybird.MoneybirdContact;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoneybirdContactTest {

    @Test
    public void fullNameToFirstAndLastNames() {

        // Checks if the first and last names are created
        // properly from two-word full name.

        MoneybirdContact contact1 = new MoneybirdContact();
        String fullName1 = "Vitality Booster";
        contact1.setFirstAndLastName(fullName1);
        assertEquals(contact1.getFirstname(), "Vitality");
        assertEquals(contact1.getLastname(), "Booster");

        // Checks if the first and last names are created
        // properly from three-word full name.
        // Note: with three and more words name creation will
        // follow similar logic.

        MoneybirdContact contact2 = new MoneybirdContact();
        String fullName2 = "Vitality Booster Thebeast";
        contact2.setFirstAndLastName(fullName2);
        assertEquals(contact2.getFirstname(), "Vitality Booster");
        assertEquals(contact2.getLastname(), "Thebeast");
    }
}
