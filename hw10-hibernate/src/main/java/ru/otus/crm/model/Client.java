package ru.otus.crm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones == null ? new ArrayList<>() : new ArrayList<>(phones);
        setOwnerForPhones();
    }

    private void setOwnerForPhones() {
        if (phones != null) {
            for (Phone phone : phones) {
                phone.setClient(this);
            }
        }
    }

    @Override
    public Client clone() {
        Address addressCopy = Objects.nonNull(this.address) ? this.address.clone() : null;
        List<Phone> phonesCopy = Objects.nonNull(this.phones)
                ? this.phones.stream().map(Phone::clone).collect(Collectors.toList())
                : new ArrayList<>();
        return new Client(this.id, this.name, addressCopy, phonesCopy);
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones == null ? new ArrayList<>() : phones;
        setOwnerForPhones();
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "', address=" + address + ", phones=" + phones + '}';
    }
}
