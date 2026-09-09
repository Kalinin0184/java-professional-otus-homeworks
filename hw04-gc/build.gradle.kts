plugins {
    java
    application
}

application {
    mainClass.set("ru.calculator.CalcDemo")
}

tasks.jar {
    archiveBaseName.set("hw04-gc")
    manifest {
        attributes["Main-Class"] = "ru.calculator.CalcDemo"
    }
}
