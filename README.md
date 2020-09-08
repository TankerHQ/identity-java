[gradle-badge]: https://img.shields.io/static/v1.svg?label=Gradle&message=compatible&color=brightgreen
[last-commit-badge]: https://img.shields.io/github/last-commit/TankerHQ/sdk-android.svg?label=Last%20commit&logo=github
[license-badge]: https://img.shields.io/badge/License-Apache%202.0-blue.svg
[license-link]: https://opensource.org/licenses/Apache-2.0
[maven-badge]: https://img.shields.io/static/v1.svg?label=Maven&message=compatible&color=brightgreen

<a href="#readme"><img src="https://tanker.io/images/github-logo.png" alt="Tanker logo" width="180" /></a>

# Tanker Identity SDK for Java

[![License][license-badge]][license-link]
![Maven][maven-badge]
![Gradle][gradle-badge]
![Last Commit][last-commit-badge]

# Installation and usage

The Tanker Identity SDK is distributed as a Maven package.

If you are using Gradle for your project, you need to patch the `build.gradle` file at the top project to add the Maven repository used by Tanker:

```groovy
allprojects {
  repositories {
    // ...
    maven {
      url 'https://storage.googleapis.com/maven.tanker.io'
    }
  }
}
```

Then add the Identity library to the list of dependencies:

```groovy
dependencies {
  implementation 'io.tanker.identity:identity:1.0.0-beta-1'
}
```


## Create identity

Create a new Tanker identity. This identity is secret and must only be given to a user who has been authenticated by your application. This identity is used by the Tanker client SDK to open a Tanker session.

```java
import io.tanker.identity.Identity;

String identity = Identity.createIdentity(appId, appSecret, userId);
```

| Parameters                 |                                                                                        |
|----------------------------|----------------------------------------------------------------------------------------|
| **appId**: *String*     | The app ID, you can access it from the [Tanker dashboard](https://dashboard.tanker.io) |
| **appSecret**: *String* | The app secret, secret that you have saved right after the creation of your app       |
| **userId**: *String*       | The unique ID of a user in your application                                            |

| Returns               |             |
|-----------------------|-------------|
| *String* | An identity |

## Provisional identity

Create a Tanker provisional identity. It allows you to share a resource with a user who does not have an account in your application yet. It must be served to an authenticated user, and attached client-side to access the resource. You will need to verifiy the associated email.

```java
String identity = Identity.createProvisionalIdentity(appId, email);
```

| Parameters             |                                                                                        |
|------------------------|----------------------------------------------------------------------------------------|
| **appId**: *String* | The app ID, you can access it from the [Tanker dashboard](https://dashboard.tanker.io) |
| **email**: *String*    | The email associated with the provisional identity                                     |

| Returns               |                        |
|-----------------------|------------------------|
| *String* | A provisional identity |

## Public identity

Return the public identity from an identity or a provisional identity. This public identity can be used by the Tanker client SDK to share encrypted resources and add users to groups.

```java
String publicIdentity = Identity.getPublicIdentity(identity);
```

| Parameters                |                                       |
|---------------------------|---------------------------------------|
| **identity**: *String* | An identity or a provisional identity |

| Returns               |                   |
|-----------------------|-------------------|
| *String* | A public identity |

# Going further

Read more about Tanker and identities in the [Tanker documentation](https://docs.tanker.io)
