package ru.otus.atm;

import java.util.Map;

public interface Atm {
    void deposit(Denomination denomination, int count);

    Map<Denomination, Integer> withdraw(int amount);

    long getBalance();
}
