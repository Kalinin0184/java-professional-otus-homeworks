package ru.otus.atm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        Atm atm = createAtm();

        System.out.println("Initial balance: " + atm.getBalance());

        atm.deposit(Denomination.ONE_THOUSAND, 2);
        atm.deposit(Denomination.FIVE_HUNDRED, 3);
        atm.deposit(Denomination.ONE_HUNDRED, 10);
        System.out.println("After deposit balance: " + atm.getBalance());

        Map<Denomination, Integer> dispensed = atm.withdraw(3700);
        System.out.println("Withdraw 3700: " + format(dispensed));
        System.out.println("Balance after withdraw: " + atm.getBalance());

        try {
            atm.withdraw(50);
        } catch (AtmException e) {
            System.out.println("Expected error: " + e.getMessage());
            System.out.println("Balance unchanged: " + atm.getBalance());
        }
    }

    private static Atm createAtm() {
        List<BanknoteCell> cells = new ArrayList<>();
        for (Denomination denomination : Denomination.values()) {
            cells.add(new DefaultBanknoteCell(denomination));
        }
        return new AtmImpl(new Cassette(cells), new GreedyWithdrawalStrategy());
    }

    private static String format(Map<Denomination, Integer> banknotes) {
        Map<Denomination, Integer> ordered = new EnumMap<>(Denomination.class);
        ordered.putAll(banknotes);
        return ordered.toString();
    }
}
