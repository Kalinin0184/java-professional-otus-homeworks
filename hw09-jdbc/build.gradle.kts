plugins {
    java
    application
}

application {
    mainClass.set("ru.otus.HomeWork")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.flywaydb:flyway-core:11.3.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.3.1")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.zaxxer:HikariCP:6.2.1")
}

tasks.jar {
    archiveBaseName.set("hw09-jdbc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "ru.otus.HomeWork"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
