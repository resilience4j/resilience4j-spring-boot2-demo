
plugins {
    application
    id("org.springframework.boot") version ("2.7.4")
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


repositories {
	mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

}

//configurations.all {
//	resolutionStrategy.cacheChangingModulesFor 0, "seconds"
//}

val resilience4jVersion = "1.7.1"

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	implementation("io.github.resilience4j:resilience4j-spring-boot2:${resilience4jVersion}")
	implementation("io.github.resilience4j:resilience4j-all:${resilience4jVersion}") // Optional, only required when you want to use the Decorators class
	implementation("io.github.resilience4j:resilience4j-reactor:${resilience4jVersion}")
	implementation("io.micrometer:micrometer-registry-prometheus")

	implementation("de.codecentric:chaos-monkey-spring-boot:2.6.1")
	
	implementation("io.vavr:vavr-jackson:0.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
