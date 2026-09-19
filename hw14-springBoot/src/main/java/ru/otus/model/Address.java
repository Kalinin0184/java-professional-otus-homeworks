package ru.otus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
public class Address {

    @Id
    private final Long id;
    private final String street;
    private final Long clientId;

    public Address(String street) {
        this(null, street, null);
    }

    @PersistenceCreator
    public Address(Long id, String street, Long clientId) {
        this.id = id;
        this.street = street;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public Long getClientId() {
        return clientId;
    }
}
