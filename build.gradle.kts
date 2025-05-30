plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "6.0.1.5171"
}

group = "org.justjava"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.keycloak:keycloak-admin-client:26.0.4")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6") // Version must be specified explicitly
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.skyscreamer:jsonassert")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("net.bytebuddy:byte-buddy-agent") // Byte-Buddy agent so Mockito can use the inline mock‐maker without self-attach
}

tasks.withType<Test> {
    useJUnitPlatform()

    doFirst { // Before the JVM forks, find the byte-buddy agent on the classpath and add it as a javaagent
        val agentJar = configurations
            .getByName("testRuntimeClasspath")
            .files
            .find { it.name.contains("byte-buddy-agent") }

        agentJar?.let {
            jvmArgs(
                "-javaagent:${it.absolutePath}",
                "-XX:+EnableDynamicAgentLoading"
            )
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // a report is always generated after tests run
}
tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.sonarqube {
    dependsOn(tasks.jacocoTestReport)
}
