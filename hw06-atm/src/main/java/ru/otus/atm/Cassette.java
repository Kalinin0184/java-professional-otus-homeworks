package ru.otus.atm;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Кассета: набор ячеек по номиналам (DIP: Atm работает с абстракцией ячеек через кассету).
 */
public class Cassette {
    private final Map<Denomination, BanknoteCell> cells = new EnumMap<>(Denomination.class);

    public Cassette(Collection<BanknoteCell> banknoteCells) {
        if (banknoteCells == null || banknoteCells.isEmpty()) {
            throw new IllegalArgumentException("cassette must contain at least one cell");
        }
        for (BanknoteCell cell : banknoteCells) {
            Denomination denomination = cell.getDenomination();
            if (cells.containsKey(denomination)) {
                throw new IllegalArgumentException("duplicate cell for denomination " + denomination);
            }
            cells.put(denomination, cell);
        }
    }

    public BanknoteCell getCell(Denomination denomination) {
        BanknoteCell cell = cells.get(denomination);
        if (cell == null) {
            throw new AtmException("No cell for denomination " + denomination);
        }
        return cell;
    }

    public Collection<BanknoteCell> getCells() {
        return cells.values();
    }

    public long getBalance() {
        long sum = 0;
        for (BanknoteCell cell : cells.values()) {
            sum += cell.getAmount();
        }
        return sum;
    }
}
