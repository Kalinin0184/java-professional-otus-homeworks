package ru.otus.atm;

public class DefaultBanknoteCell implements BanknoteCell {
    private final Denomination denomination;
    private int count;

    public DefaultBanknoteCell(Denomination denomination) {
        this(denomination, 0);
    }

    public DefaultBanknoteCell(Denomination denomination, int initialCount) {
        if (denomination == null) {
            throw new IllegalArgumentException("denomination must not be null");
        }
        if (initialCount < 0) {
            throw new IllegalArgumentException("initialCount must be >= 0");
        }
        this.denomination = denomination;
        this.count = initialCount;
    }

    @Override
    public Denomination getDenomination() {
        return denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public long getAmount() {
        return (long) count * denomination.getValue();
    }

    @Override
    public void put(int banknotes) {
        if (banknotes <= 0) {
            throw new IllegalArgumentException("banknotes to put must be > 0");
        }
        count += banknotes;
    }

    @Override
    public void take(int banknotes) {
        if (banknotes <= 0) {
            throw new IllegalArgumentException("banknotes to take must be > 0");
        }
        if (banknotes > count) {
            throw new AtmException("Not enough banknotes of denomination " + denomination);
        }
        count -= banknotes;
    }
}
