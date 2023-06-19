package com.example.demo.services;

import com.example.demo.entities.Contact;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.repositories.IContactRepository;
import com.example.demo.services.interfaces.IInvoicerContactService;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoicerContactService implements IInvoicerContactService {

    private final IMoneybirdContactService contactService;
    private final IContactRepository contactRepository;

    // This method tries to find a contact in a DB. If the contact is found,
    // method sets its id for invoice. Otherwise, a new contact is created
    // on Moneybird, its id is retrieved and set for invoice and the contact
    // entity is added to a Contact table in the DB
    @Override
    public void setContactIdForInvoice(SalesInvoice invoice,
                                        EtsyReceipt receipt) {

        // try to find the contact in the DB
        Contact entityContact = entityContactFromReceipt(receipt);
        Example<Contact> example = Example.of(entityContact);
        Optional<Contact> contactFromRepo = contactRepository.findOne(example);

        Long id;

        // if contact is found, retrieve its id
        if (contactFromRepo.isPresent()) {
            id = contactFromRepo.get().getMoneybirdContactId();
        }
        // otherwise create a new contact on MB, retrieve its id and add the contact to the table
        else {
            MoneybirdContact contact = moneybirdContactFromReceipt(receipt);
            id = contactService.createContact(contact).block().getId();

            entityContact.setMoneybirdContactId(id);
            contactRepository.save(entityContact);
        }

        invoice.setContactId(id);
    }

    @Override
    public void updateContactTable() {
        Iterable<MoneybirdContact> contactIterable =
                contactService.getAllContacts().toIterable();

        for (MoneybirdContact moneybirdContact : contactIterable) {
            boolean isRecordAdded = !contactRepository
                    .findByMoneybirdContactId(moneybirdContact.getId())
                    .isEmpty();
            if (!isRecordAdded) {
                contactRepository.save(Contact.builder()
                        .moneybirdContactId(moneybirdContact.getId())
                        .city(moneybirdContact.getCity())
                        .address1(moneybirdContact.getAddress1())
                        .address2(moneybirdContact.getAddress2())
                        .countryIso(moneybirdContact.getCountry())
                        .firstName(moneybirdContact.getFirstname())
                        .lastName(moneybirdContact.getLastname())
                        .zipCode(moneybirdContact.getZipcode())
                        .build());
            }
        }
    }

    private MoneybirdContact moneybirdContactFromReceipt(EtsyReceipt receipt) {
        MoneybirdContact contact = new MoneybirdContact();

        // Setting up address
        contact.setAddress1(receipt.getFirstLine());
        contact.setAddress2(receipt.getSecondLine());
        contact.setCity(receipt.getCity());
        contact.setZipcode(receipt.getZip());

        // Getting a country from its ISO
        contact.setCountry(receipt.getCountryIso());
        // End of address part

        // Setting first and last names
        contact.setFirstAndLastName(receipt.getName());

        // Decide if we'll set a tax_rate_id.
        // Add the rest of the fields and create a contact with the Service

        return contact;
    }
    
    private Contact entityContactFromReceipt(EtsyReceipt receipt) {
        String fullName = receipt.getName().trim();
        String firstName, lastName = null;

        if (!fullName.contains(" ")) {
            firstName = fullName;
        }
        else {
            firstName = fullName.substring(0, fullName.lastIndexOf(" ")).trim();
            lastName = fullName.substring(fullName.lastIndexOf(" ")).trim();
        }

        return Contact.builder()
                .address1(receipt.getFirstLine())
                .address2(receipt.getSecondLine())
                .city(receipt.getCity())
                .zipCode(receipt.getZip())
                .countryIso(receipt.getCountryIso())
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
