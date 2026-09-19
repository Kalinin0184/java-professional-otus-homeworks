plugins {
    java
    application
}

application {
    mainClass.set("ru.otus.demo.DbServiceDemo")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.hibernate:hibernate-core:5.6.15.Final")
    implementation("javax.persistence:javax.persistence-api:2.2")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("net.bytebuddy:byte-buddy:1.14.19")

    testImplementation("com.h2database:h2:2.3.232")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
