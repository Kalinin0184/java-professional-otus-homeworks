package ru.otus.atm;

/**
 * Номиналы банкнот. Порядок объявления — от большего к меньшему,
 * чтобы жадная выдача брала сначала крупные купюры.
 */
public enum Denomination {
    FIVE_THOUSAND(5000),
    TWO_THOUSAND(2000),
    ONE_THOUSAND(1000),
    FIVE_HUNDRED(500),
    TWO_HUNDRED(200),
    ONE_HUNDRED(100),
    FIFTY(50);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
