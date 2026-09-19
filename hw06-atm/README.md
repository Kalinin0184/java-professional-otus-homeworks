# hw06-atm — Эмулятор банкомата (SOLID)

## Возможности ATM

- принимать банкноты разных номиналов (своя ячейка на номинал)
- выдавать сумму минимальным числом купюр (жадный алгоритм) или ошибку
- показывать остаток

## Архитектура

| Класс / интерфейс | Роль | SOLID |
|---|---|---|
| `Atm` / `AtmImpl` | фасад банкомата | DIP — зависит от `Cassette` и `WithdrawalStrategy` |
| `BanknoteCell` / `DefaultBanknoteCell` | ячейка одного номинала | SRP |
| `Cassette` | набор ячеек | SRP |
| `WithdrawalStrategy` / `GreedyWithdrawalStrategy` | подбор купюр | OCP — алгоритм можно заменить |
| `Denomination` | номиналы | — |

Выдача сначала строит план, и только потом снимает купюры с ячеек — при ошибке баланс не меняется.

## Запуск

```bat
gradlew.bat :hw06-atm:build
java -jar hw06-atm\build\libs\hw06-atm-1.0.jar
```
