plugins {
    java
}

dependencies {
    implementation("com.google.guava:guava:33.7.1-jre")
}

tasks.jar {
    archiveBaseName.set("hw01-gradle")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "ru.otus.HelloOtus"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
