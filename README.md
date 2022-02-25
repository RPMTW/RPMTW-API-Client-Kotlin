[![codecov](https://codecov.io/gh/RPMTW/RPMTW-API-Client-Kotlin/branch/main/graph/badge.svg?token=LLbPksdbHb)](https://codecov.io/gh/RPMTW/RPMTW-API-Client-Kotlin)
[![](https://jitpack.io/v/RPMTW/RPMTW-API-Client-Kotlin.svg)](https://jitpack.io/#RPMTW/RPMTW-API-Client-Kotlin)

# RPMTW API Client For Kotlin

## Example

```kotlin
import com.rpmtw.rpmtw_api_client.RPMTWApiClient

fun main(args: Array<String>) {
    val client = RPMTWApiClient.init() // Initialize RPMTW API Client
}
```

## How to use

[![Github tag](https://img.shields.io/github/tag/RPMTW/RPMTW-API-Client-Kotlin.svg)](https://github.com/RPMTW/RPMTW-API-Client-Kotlin/tags/)

### Gradle (groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.RPMTW:RPMTW-API-Client-Kotlin:tag'
}
```

### Gradle (kotlin dsl)

```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.RPMTW:RPMTW-API-Client-Kotlin:tag")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.RPMTW</groupId>
    <artifactId>RPMTW-API-Client-Kotlin</artifactId>
    <version>tag</version>
</dependency>
```

### [Jitpack](https://jitpack.io/#RPMTW/RPMTW-API-Client-Kotlin)

## Other Languages

[Dart](https://github.com/RPMTW/RPMTW-API-Client)