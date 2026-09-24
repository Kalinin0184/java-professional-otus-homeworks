plugins {
    java
    idea
    id("com.google.protobuf") version "0.9.4"
}

val grpcVersion = "1.68.1"
val protobufVersion = "3.25.5"

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/grpc"
            )
        }
    }
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Starts the gRPC numbers server"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ru.otus.numbers.server.NumbersServer")
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Starts the gRPC numbers client"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ru.otus.numbers.client.NumbersClient")
}
