# Maya Wallet Demo

A simple Android wallet demo application built as part of a technical
assessment.

The app demonstrates **Clean Architecture**, **MVVM**, **Hilt dependency
injection**, **API integration**, and **unit testing with TDD**.

------------------------------------------------------------------------

# Tech Stack

-   Kotlin
-   Jetpack Compose
-   Clean Architecture
-   MVVM
-   Hilt (Dependency Injection)
-   Retrofit (API calls)
-   Moshi (JSON parsing)
-   Kotlin Coroutines / Flow
-   JUnit4 (Unit Testing)

------------------------------------------------------------------------

# Features

## 1. Login Screen

-   User logs in with username and password
-   Session is stored in memory for demo purposes

## 2. Wallet Screen

-   Displays wallet balance (**500 PHP**)
-   Toggle balance visibility
-   Navigate to:
    -   Send Money
    -   Transactions
-   Logout support

## 3. Send Money Screen

-   Accepts numeric input only
-   Validates that the amount is **less than or equal to the wallet
    balance**
-   Shows **success or failure bottom sheet** after submission

## 4. Transactions Screen

-   Fetches transactions from a **mock REST API**
-   Displays list of sent transactions

------------------------------------------------------------------------

# Architecture

The project follows **Clean Architecture** with clear separation of
layers.

    app/
     ├── app/                  Application + MainActivity
     ├── di/                   Hilt modules
     ├── navigation/           Navigation graph
     ├── core/                 Shared utilities
     ├── data/
     │    ├── remote/          Retrofit API + DTO
     │    ├── mapper/          DTO → Domain mapper
     │    └── repository/      Repository implementations
     ├── domain/
     │    ├── model/           Domain models
     │    ├── repository/      Repository interfaces
     │    └── usecase/         Business logic
     └── presentation/
          ├── auth/
          ├── wallet/
          ├── sendmoney/
          └── transactions/

## Layer Responsibilities

### Presentation

-   UI (Jetpack Compose)
-   ViewModels
-   UI state

### Domain

-   Business logic
-   Use cases
-   Repository interfaces

### Data

-   API implementation
-   Repository implementations
-   DTO mapping

------------------------------------------------------------------------

# API

Transactions are retrieved from a **mock REST API**.

Example endpoints:

    GET /transactions
    POST /transactions

Example response:

``` json
{
  "id": "1",
  "amount": 100,
  "currency": "PHP",
  "createdAt": "2026-02-27T11:00:00Z"
}
```

------------------------------------------------------------------------

# How to Run the Project

## 1. Clone the repository

``` bash
git clone <repository-url>
```

Example:

``` bash
git clone https://github.com/your-username/maya-wallet-demo.git
```

------------------------------------------------------------------------

## 2. Open in Android Studio

Open the project folder using **Android Studio Hedgehog or newer**.

------------------------------------------------------------------------

## 3. Sync Gradle

Android Studio will prompt to sync Gradle dependencies.

Wait until the sync completes.

------------------------------------------------------------------------

## 4. Run the App

Select an emulator or connected Android device.

Press the **Run ▶ button** or use the shortcut:

    Shift + F10

------------------------------------------------------------------------

# Running Unit Tests

## Run All Tests in Android Studio

1.  Right-click the `test` directory:

    app/src/test

2.  Click:

    Run 'All Tests'

------------------------------------------------------------------------

## Run Tests via Gradle

From the project root directory:

``` bash
./gradlew test
```

Windows:

``` bash
gradlew test
```

------------------------------------------------------------------------

## Test Report

After running tests, the report is generated at:

    app/build/reports/tests/testDebugUnitTest/index.html

The report includes:

-   Passed tests
-   Failed tests
-   Execution time
-   Stack traces

------------------------------------------------------------------------

# Unit Testing

The project uses **JUnit4** with **Coroutine Test Dispatchers**.

Tests cover the following ViewModels:

-   LoginViewModel
-   WalletViewModel
-   SendMoneyViewModel
-   TransactionsViewModel

## Example Test Cases

### WalletViewModel

-   Load initial wallet balance
-   Toggle balance visibility
-   Navigate to send money
-   Navigate to transactions
-   Logout

### SendMoneyViewModel

-   Validate numeric input
-   Reject invalid amount
-   Reject amount greater than balance
-   Emit success event on valid send

### TransactionsViewModel

-   Load transactions from repository
-   Handle API failure
-   Logout

------------------------------------------------------------------------

# TDD Approach

The project follows **Test Driven Development** where possible.

Typical workflow:

1.  Write failing test
2.  Implement feature
3.  Refactor
4.  Ensure tests pass

------------------------------------------------------------------------

# Demo Wallet Balance

For demonstration purposes, the wallet balance is fixed at:

    500 PHP

------------------------------------------------------------------------

# Author

**Jomar Olaybal**\
Senior Android Developer

LinkedIn:\
https://www.linkedin.com/in/jomarolaybal/

------------------------------------------------------------------------

# Notes

This project was built for demonstration purposes and focuses on:

-   Code structure
-   Testability
-   Clean architecture
-   Separation of concerns
