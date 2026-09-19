package ru.otus.atm;

import java.util.EnumMap;
import java.util.Map;

/**
 * Жадный алгоритм: берём максимально возможные крупные купюры.
 * Оптимизировать выдачу по заданию не требуется.
 */
public class GreedyWithdrawalStrategy implements WithdrawalStrategy {

    @Override
    public Map<Denomination, Integer> plan(Cassette cassette, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (amount > cassette.getBalance()) {
            throw new AtmException("Requested amount exceeds ATM balance");
        }

        Map<Denomination, Integer> plan = new EnumMap<>(Denomination.class);
        int remaining = amount;

        for (Denomination denomination : Denomination.values()) {
            BanknoteCell cell;
            try {
                cell = cassette.getCell(denomination);
            } catch (AtmException ignored) {
                continue;
            }

            int available = cell.getCount();
            int needed = remaining / denomination.getValue();
            int take = Math.min(available, needed);
            if (take > 0) {
                plan.put(denomination, take);
                remaining -= take * denomination.getValue();
            }
        }

        if (remaining != 0) {
            throw new AtmException("Cannot dispense amount " + amount + " with available banknotes");
        }
        return plan;
    }
}
