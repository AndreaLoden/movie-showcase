# Movie Showcase App

A Kotlin Multiplatform application using [The Movie Database (TMDb)](https://www.themoviedb.org/) API to display movies with UI powered by **Compose Multiplatform**.  
Built with **KMP**, **Compose Multiplatform**, **Coroutines**, **Flow**, and clean architecture principles.

## Features

- Fetch movies by release date with paginatin support
- Search movies from TMDb
- View detailed movie info
- Clean architecture (Domain, Data, Presentation layers)
- Compose UI across Android and iOS
- PR Verification with Codecov for test coverage and ktlint for code formatting

## Tech Stack

| Layer            | Technology                                   |
|------------------|----------------------------------------------|
| UI               | Compose Multiplatform                        |
| Shared Logic     | Kotlin Multiplatform                         |
| Networking       | Ktor Client (KMP HTTP client)                |
| DI               | Koin                                         |
| State Management | Kotlin Coroutines + Flow                     |
| Testing          | kotlinx-coroutines-test, Mokkery             |


## Making it work
Use your TMDb key to acces their API, copy-paste it into Credentials.kt 

## Missing/Next Steps/To do
- Compose Preview Tooling
- UI test, composables in Compose Multiplatform can only be unit tested with iOS target, or with instrumented tests separately Android/iOS
- Navigation a bit clunky, no safe-args -> Could use a sealed class or object for typed navigation
- App icon, app name, fonts, theme
- Logging
