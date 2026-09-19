package ru.otus.atm;

import java.util.Map;

/**
 * Стратегия подбора купюр (OCP: можно заменить алгоритм без изменения Atm).
 */
public interface WithdrawalStrategy {
    /**
     * @return план выдачи: номинал -> количество купюр
     */
    Map<Denomination, Integer> plan(Cassette cassette, int amount);
}
