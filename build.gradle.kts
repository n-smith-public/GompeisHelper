plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("com.greenbueller.DenBot.DenBot")
}


group = "org.greenbueller"
version = "1.0"

val jdaVersion = "5.0.0-beta.21"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    sourceCompatibility = "1.8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    archiveFileName.set("DenBot-1.0-all.jar")
}