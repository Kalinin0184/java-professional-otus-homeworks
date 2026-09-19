package ru.otus.dto;

import org.junit.jupiter.api.Test;
import ru.otus.model.Client;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFormTest {

    @Test
    void shouldMapFormToClientAggregate() {
        ClientForm form = new ClientForm();
        form.setName("Ivan");
        form.setStreet("Lenina");
        form.setPhones("11-111, 22-222");

        Client client = form.toClient();

        assertThat(client.getName()).isEqualTo("Ivan");
        assertThat(client.getAddress().getStreet()).isEqualTo("Lenina");
        assertThat(client.getPhones()).extracting("number").containsExactlyInAnyOrder("11-111", "22-222");
    }
}
