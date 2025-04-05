tasks.register<Test>("debug") {
    group = "tests"
    useJUnitPlatform {
        includeTags("Debug")
    }
}