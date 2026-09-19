package ru.otus.aop;

public class Demo {

    public static void main(String[] args) {
        TestLoggingInterface logging = Ioc.createLoggedInstance(new TestLogging(), TestLoggingInterface.class);

        logging.calculation(6);
        logging.calculation(6, 7);
        logging.calculation(6, 7, "eight");
        logging.calculationWithoutLog(100);
    }
}
