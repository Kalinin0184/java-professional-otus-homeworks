package ru.otus.dto;

import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientForm {
    private Long id;
    private String name;
    private String street;
    private String phones;

    public ClientForm() {
    }

    public ClientForm(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.street = client.getAddress() == null ? "" : client.getAddress().getStreet();
        this.phones = client.getPhones() == null
                ? ""
                : client.getPhones().stream().map(Phone::getNumber).collect(Collectors.joining(", "));
    }

    public Client toClient() {
        Address address = (street == null || street.isBlank()) ? null : new Address(street.trim());
        Set<Phone> phoneSet = parsePhones(phones);
        return new Client(name, address, phoneSet);
    }

    private static Set<Phone> parsePhones(String phones) {
        if (phones == null || phones.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(phones.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Phone::new)
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "ClientForm{id=" + id + ", name='" + name + "', street='" + street + "', phones='" + phones + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientForm that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(street, that.street)
                && Objects.equals(phones, that.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, street, phones);
    }
}
