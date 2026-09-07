plugins {
    java
}

tasks.jar {
    archiveBaseName.set("hw02-collections")
    manifest {
        attributes["Main-Class"] = "ru.otus.collections.Main"
    }
}
