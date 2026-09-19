plugins {
    java
    application
}

application {
    mainClass.set("ru.otus.atm.Demo")
}

tasks.jar {
    archiveBaseName.set("hw06-atm")
    manifest {
        attributes["Main-Class"] = "ru.otus.atm.Demo"
    }
}
