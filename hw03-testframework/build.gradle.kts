plugins {
    java
}

tasks.jar {
    archiveBaseName.set("hw03-testframework")
    manifest {
        attributes["Main-Class"] = "ru.otus.testframework.Main"
    }
}
