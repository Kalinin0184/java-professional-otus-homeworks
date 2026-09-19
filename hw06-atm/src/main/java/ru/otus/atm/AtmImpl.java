package ru.otus.atm;

import java.util.Map;

public class AtmImpl implements Atm {
    private final Cassette cassette;
    private final WithdrawalStrategy withdrawalStrategy;

    public AtmImpl(Cassette cassette, WithdrawalStrategy withdrawalStrategy) {
        if (cassette == null) {
            throw new IllegalArgumentException("cassette must not be null");
        }
        if (withdrawalStrategy == null) {
            throw new IllegalArgumentException("withdrawalStrategy must not be null");
        }
        this.cassette = cassette;
        this.withdrawalStrategy = withdrawalStrategy;
    }

    @Override
    public void deposit(Denomination denomination, int count) {
        cassette.getCell(denomination).put(count);
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount) {
        Map<Denomination, Integer> plan = withdrawalStrategy.plan(cassette, amount);
        for (Map.Entry<Denomination, Integer> entry : plan.entrySet()) {
            cassette.getCell(entry.getKey()).take(entry.getValue());
        }
        return plan;
    }

    @Override
    public long getBalance() {
        return cassette.getBalance();
    }
}
