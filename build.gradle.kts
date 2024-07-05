plugins {
    id("java")
}

group = "burrow"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/burrow-1.0.0-all.jar"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}