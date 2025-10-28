plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.4"
val lwjglNatives = "natives-linux"

dependencies {
    // LWJGL Core
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    
    // LWJGL Modules
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    implementation("org.lwjgl:lwjgl-assimp")
    
    // LWJGL Natives
    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    
    // GLM (OpenGL Mathematics) - Java port
    implementation("org.joml:joml:1.10.5")
    
    // Gson для парсинга JSON (glTF)
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Kotlin Coroutines для асинхронного программирования
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    
    // Тестирование
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Настройка JVM аргументов для LWJGL
tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "-Djava.awt.headless=true"
    )
}

// Настройка для запуска приложения
application {
    mainClass.set("org.example.MainKt")
}