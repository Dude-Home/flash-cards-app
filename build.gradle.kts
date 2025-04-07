import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

description = "Simple Flashcards Application"
group = "org.personal.dude"

val subProjects = listOf(
    "flash-cards-api",
//    "flash-cards-ui"
)

val javaProjects = listOf(
    "flash-cards-api"
)

plugins {
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.dependency.management)

    alias(libs.plugins.gradle.liquibase)

    java
    `java-library`
    `maven-publish`
    pmd
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

dependencies {
    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.mysql)
}

liquibase {
    activities {
        all {
            properties {
                changeLogFile.set("db-changelog.mysql.yaml")
                driver.set("com.mysql.jdbc.Driver")
            }
        }

        register("deploy") {
            properties {
                url.set("jdbc:mysql://server-name:server-port/database-name")
                username.set(System.getenv("DB_USER"))
                password.set(System.getenv("DB_PASSWORD"))
            }
        }
    }
}

tasks.withType(Wrapper::class) {
    gradleVersion = "8.5"
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.register<Copy>("installGitHooks") {
    group = "git hooks"
    from(fileTree("${rootProject.projectDir}/.scripts/git/hooks"))
    into("${rootProject.projectDir}/.git/hooks")
    filePermissions {
        user {
            write = true
            execute = true
        }
        group {
            write = true
            execute = true
        }
        other.execute = true
    }
}

tasks.named("build") {
    dependsOn("installGitHooks")
}

configure(javaProjects) {
    group = "org.personal.group"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "com.diffplug.spotless")

    pmd {
        isConsoleOutput = true
        toolVersion = "7.12.0"
        rulesMinimumPriority = 5
        threads = 4
    }

    spotbugs {
        toolVersion = "4.9.3"
        ignoreFailures = false
        showStackTraces = true
        showProgress = true
    }

    spotless {
        java {
            googleJavaFormat().aosp()
            importOrder()
            removeUnusedImports()
            endWithNewline()
            toggleOffOn()
            formatAnnotations()
        }
        format("misc") {
            target(
                "*.yml",
                "*.yaml",
                "*.gradle",
                "*.gitignore",
                "README.md",
                "src/**/*.xml",
                "src/**/*.json"
            )
            trimTrailingWhitespace()
            endWithNewline()
        }
        encoding("UTF-8")
    }
}

configure(javaProjects.map { project(":$it") }) {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        isScanForTestClasses = false
        defaultCharacterEncoding = "UTF-8"

        testLogging {
            events = setOf(PASSED, FAILED, SKIPPED)
            showStandardStreams = true
            exceptionFormat = FULL
        }

        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
    }
}