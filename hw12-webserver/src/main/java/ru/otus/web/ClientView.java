package ru.otus.web;

import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.stream.Collectors;

public class ClientView {
    private final Long id;
    private final String name;
    private final String street;
    private final String numbers;

    public ClientView(Long id, String name, String street, String numbers) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.numbers = numbers;
    }

    public static ClientView from(Client client) {
        String street = client.getAddress() == null ? "" : client.getAddress().getStreet();
        String numbers = client.getPhones() == null
                ? ""
                : client.getPhones().stream().map(Phone::getNumber).collect(Collectors.joining(", "));
        return new ClientView(client.getId(), client.getName(), street, numbers);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getNumbers() {
        return numbers;
    }
}
