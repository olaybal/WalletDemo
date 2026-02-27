# Design Documentation

This document provides a **Class Diagram** and **Sequence Diagrams** for the Maya Wallet Demo app.

> Diagram format: **Mermaid**.

---

## Class Diagram

```mermaid
classDiagram
direction LR

%% =========================
%% Presentation Layer
%% =========================
class LoginViewModel {
  -StateFlow<LoginUiState> state
  -SharedFlow<LoginEvent> events
  +onUsernameChange(String)
  +onPasswordChange(String)
  +onLoginClick()
}

class WalletViewModel {
  -StateFlow<WalletUiState> state
  -SharedFlow<WalletEvent> events
  +onToggleBalanceVisibility()
  +onSendMoneyClick()
  +onViewTransactionsClick()
  +onLogoutClick()
}

class SendMoneyViewModel {
  -StateFlow<SendMoneyUiState> state
  -SharedFlow<SendMoneyEvent> events
  +onAmountChange(String)
  +onSubmit()
  +onLogoutClick()
}

class TransactionsViewModel {
  -StateFlow<TransactionsUiState> state
  -SharedFlow<TransactionsEvent> events
  +onRetry()
  +onLogoutClick()
}

%% =========================
%% Domain Layer
%% =========================
class SessionRepository {
  <<interface>>
  +login(String,String) Result~Session~
  +logout()
  +getSession() Session?
}

class WalletRepository {
  <<interface>>
  +getBalance() WalletBalance
  +sendMoney(Double) Result~Unit~
  +getTransactions() List~Transaction~
}

class LoginUseCase {
  +invoke(String,String) Result~Session~
}
class LogoutUseCase {
  +invoke()
}
class GetWalletBalanceUseCase {
  +invoke() WalletBalance
}
class SendMoneyUseCase {
  +invoke(Double) Result~Unit~
}
class GetTransactionsUseCase {
  +invoke() List~Transaction~
}

class Session {
  +token: String
  +username: String
}
class WalletBalance {
  +amount: Double
  +currency: String
}
class Transaction {
  +id: String
  +amount: Double
  +currency: String
  +createdAt: String?
}

%% =========================
%% Data Layer
%% =========================
class InMemorySessionRepositoryImpl {
  +login(String,String) Result~Session~
  +logout()
  +getSession() Session?
}

class ApiWalletRepositoryImpl {
  +getBalance() WalletBalance
  +sendMoney(Double) Result~Unit~
  +getTransactions() List~Transaction~
}

class TransactionsApi {
  <<interface>>
  +getTransactions() List~TransactionDto~
  +createTransaction(TransactionDto) TransactionDto
}

class TransactionDto {
  +id: String?
  +amount: Double?
  +currency: String?
  +createdAt: String?
}

%% =========================
%% Relationships
%% =========================
LoginViewModel --> LoginUseCase
LoginUseCase --> SessionRepository

WalletViewModel --> GetWalletBalanceUseCase
WalletViewModel --> LogoutUseCase
GetWalletBalanceUseCase --> WalletRepository
LogoutUseCase --> SessionRepository

SendMoneyViewModel --> SendMoneyUseCase
SendMoneyViewModel --> LogoutUseCase
SendMoneyUseCase --> WalletRepository

TransactionsViewModel --> GetTransactionsUseCase
TransactionsViewModel --> LogoutUseCase
GetTransactionsUseCase --> WalletRepository

InMemorySessionRepositoryImpl ..|> SessionRepository
ApiWalletRepositoryImpl ..|> WalletRepository
ApiWalletRepositoryImpl --> TransactionsApi
TransactionsApi --> TransactionDto
TransactionDto --> Transaction

SessionRepository --> Session
WalletRepository --> WalletBalance
WalletRepository --> Transaction
```

---

## Sequence Diagrams

### 1) App Start → Wallet loads balance

```mermaid
sequenceDiagram
autonumber
actor User
participant WalletScreen
participant WalletViewModel
participant GetWalletBalanceUseCase
participant WalletRepository

User->>WalletScreen: Open Wallet
WalletScreen->>WalletViewModel: init
WalletViewModel->>GetWalletBalanceUseCase: invoke()
GetWalletBalanceUseCase->>WalletRepository: getBalance()
WalletRepository-->>GetWalletBalanceUseCase: WalletBalance(500, PHP)
GetWalletBalanceUseCase-->>WalletViewModel: WalletBalance
WalletViewModel-->>WalletScreen: state updated (balance shown)
```

---

### 2) Wallet → Toggle balance visibility

```mermaid
sequenceDiagram
autonumber
actor User
participant WalletScreen
participant WalletViewModel

User->>WalletScreen: Tap eye icon
WalletScreen->>WalletViewModel: onToggleBalanceVisibility()
WalletViewModel-->>WalletScreen: state updated (masked/unmasked)
```

---

### 3) Send Money → Validate and POST transaction (Mock API)

```mermaid
sequenceDiagram
autonumber
actor User
participant SendMoneyScreen
participant SendMoneyViewModel
participant GetWalletBalanceUseCase
participant SendMoneyUseCase
participant WalletRepository
participant TransactionsApi

User->>SendMoneyScreen: Enter amount
SendMoneyScreen->>SendMoneyViewModel: onAmountChange(text)
SendMoneyViewModel-->>SendMoneyScreen: state updated

User->>SendMoneyScreen: Tap Submit
SendMoneyScreen->>SendMoneyViewModel: onSubmit()

SendMoneyViewModel->>GetWalletBalanceUseCase: invoke()
GetWalletBalanceUseCase->>WalletRepository: getBalance()
WalletRepository-->>GetWalletBalanceUseCase: WalletBalance(500, PHP)
GetWalletBalanceUseCase-->>SendMoneyViewModel: balance

alt amount invalid OR amount > balance
  SendMoneyViewModel-->>SendMoneyScreen: emit ShowResultSheet(failure)
else amount valid
  SendMoneyViewModel->>SendMoneyUseCase: invoke(amount)
  SendMoneyUseCase->>WalletRepository: sendMoney(amount)
  WalletRepository->>TransactionsApi: POST /transactions
  TransactionsApi-->>WalletRepository: TransactionDto(created)
  WalletRepository-->>SendMoneyUseCase: Result.success
  SendMoneyUseCase-->>SendMoneyViewModel: Result.success
  SendMoneyViewModel-->>SendMoneyScreen: emit ShowResultSheet(success)
end
```

---

### 4) Transactions → GET list and render

```mermaid
sequenceDiagram
autonumber
actor User
participant TransactionsScreen
participant TransactionsViewModel
participant GetTransactionsUseCase
participant WalletRepository
participant TransactionsApi

User->>TransactionsScreen: Open Transactions
TransactionsScreen->>TransactionsViewModel: init
TransactionsViewModel->>GetTransactionsUseCase: invoke()
GetTransactionsUseCase->>WalletRepository: getTransactions()
WalletRepository->>TransactionsApi: GET /transactions
TransactionsApi-->>WalletRepository: List<TransactionDto>
WalletRepository-->>GetTransactionsUseCase: List<Transaction>
GetTransactionsUseCase-->>TransactionsViewModel: List<Transaction>
TransactionsViewModel-->>TransactionsScreen: state updated (list shown)
```

---

### 5) Logout (any screen)

```mermaid
sequenceDiagram
autonumber
actor User
participant AnyScreen
participant AnyViewModel
participant LogoutUseCase
participant SessionRepository

User->>AnyScreen: Tap Logout
AnyScreen->>AnyViewModel: onLogoutClick()
AnyViewModel->>LogoutUseCase: invoke()
LogoutUseCase->>SessionRepository: logout()
SessionRepository-->>LogoutUseCase: done
LogoutUseCase-->>AnyViewModel: done
AnyViewModel-->>AnyScreen: emit LoggedOut event
```

---

## Notes / Assumptions

- **Wallet balance is fixed at 500 PHP** as per exam requirement.
- **Transactions** are stored via a mock REST API (e.g., MockAPI.io) using `POST /transactions` and retrieved using `GET /transactions`.
- ViewModels expose:
  - `StateFlow<UiState>` for UI rendering
  - `SharedFlow<Event>` for one-off navigation / bottom sheets.
