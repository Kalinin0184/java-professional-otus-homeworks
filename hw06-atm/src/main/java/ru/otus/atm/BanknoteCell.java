package ru.otus.atm;

/**
 * Ячейка одного номинала (SRP: хранит только банкноты своего номинала).
 */
public interface BanknoteCell {
    Denomination getDenomination();

    int getCount();

    long getAmount();

    void put(int count);

    void take(int count);
}
