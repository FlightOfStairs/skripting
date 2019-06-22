[![Build Status](https://travis-ci.org/FlightOfStairs/skripting.svg?branch=master)](https://travis-ci.org/FlightOfStairs/skripting) [![License](https://img.shields.io/github/license/FlightOfStairs/skripting.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Skripting

Skripting is a Kotlin library and DSL intended to enable easy use of existing shell commands without having to descend to using a traditional shell scripting language.

Skripting is heavily inspired by the excellent Python library [Plumbum](https://github.com/tomerfiliba/plumbum).

## Examples

### Basics

```kotlin
skript {
    val result = "ls"("-a")
    
    println(result.trim().split("\n"))
    // [., .., .git, .gitignore, .gradle, .idea, .kotlintest, .travis.yml, LICENSE, README.md, ...]
}
```

### Piping

```kotlin
val result = skript {
    chain { "ls"(dir.absolutePath); "grep"("txt"); "wc"("-l") }
}

println(result) // "2\n"
```

### Redirection

```kotlin
val result = skript {
    pipeIn("hello world") { "cat"() }
}

println(result) // "hello world"
```
