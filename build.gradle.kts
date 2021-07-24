plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.bristermitten"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = sourceCompatibility
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.codemc.org/repository/maven-public/") // for qualityarmory
    maven("https://repo.viaversion.com") // for qualityarmory
    maven("https://repo.citizensnpcs.co") // also for qualityarmory
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("me.zombie_striker:QualityArmory:1.1.169")
    compileOnly("org.jetbrains:annotations:21.0.1")

    compileOnly("me.clip:placeholderapi:2.10.10")

    implementation("com.google.inject:guice:5.0.1")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("io.vavr:vavr:0.9.0")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("com.github.Redempt:Crunch:master")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}
tasks {

    test {
        useJUnitPlatform()
    }

    shadowJar {
        relocate("com.google", "me.bristermitten.warzone.com.google")
    }
}
