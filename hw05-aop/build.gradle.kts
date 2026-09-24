plugins {
    java
    application
}

application {
    mainClass.set("ru.otus.aop.Demo")
}

tasks.jar {
    archiveBaseName.set("hw05-aop")
    manifest {
        attributes["Main-Class"] = "ru.otus.aop.Demo"
    }
}
