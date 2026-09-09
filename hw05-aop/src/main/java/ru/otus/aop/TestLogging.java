package ru.otus.aop;

import ru.otus.aop.annotation.Log;

public class TestLogging implements TestLoggingInterface {

    @Log
    @Override
    public void calculation(int param1) {
        // business logic placeholder
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        // business logic placeholder
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        // business logic placeholder
    }

    @Override
    public void calculationWithoutLog(int param1) {
        // without @Log — should not print parameters
    }
}
