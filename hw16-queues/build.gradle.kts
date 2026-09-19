plugins {
    java
    application
}

application {
    mainClass.set("ru.otus.Main")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
    testImplementation("net.bytebuddy:byte-buddy:1.17.6")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
    testLogging {
        events("passed", "skipped", "failed")
    }
}
