//file:noinspection VulnerableLibrariesLocal

description = "GeekSMP"
version = "1.2.3"
group = "com.commandgeek"

plugins {
    id("java")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("org.javacord:javacord:3.4.0")
}
