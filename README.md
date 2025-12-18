# 🧩 P2P Sports Betting Exchange (Student Project)

## Overview
This repository contains a peer-to-peer (P2P) sports betting exchange built as a learning-focused student project, with a strong emphasis on core business logic, correctness, and architecture. 

Unlike traditional bookmakers, this system allows users to bet against each other, not against the platform.

* **Users define their own odds**
* **The platform takes no betting risk**
* **Funds are handled via explicit reservation-based accounting**
* **Settlement is deterministic and transparent**

The primary goal of this project is not to ship a commercial product, but to deeply understand and implement the mechanics of an exchange-style betting system, step by step, starting from a clean, framework-agnostic core.

---

## 🎯 Project Goals
This project is intentionally designed to achieve the following:

### 1️⃣ Learn by building the core, not the shell
Most projects start with frameworks, databases, and UI. This one starts with domain logic:
* What does an offer really represent?
* What happens when an offer is partially filled?
* How do asymmetric risks work?
* When does money actually move?
* How do we prevent double-spending?

### 2️⃣ Model an exchange, not a bookmaker
Key characteristics of an exchange-style system:
* Users bet **FOR** or **AGAINST** outcomes.
* Odds are user-defined.
* The platform does not set prices or take risk.
* External bookmaker odds (future) are reference-only.

### 3️⃣ Practice clean, evolvable architecture
The project follows a domain-first, clean architecture style:
* Business rules are isolated.
* Use cases are explicit.
* Infrastructure is replaceable.
* Frameworks are delayed on purpose.

---

## 🧠 High-level Architecture



```text
┌──────────────────────────┐
│      Infrastructure      │
│  (in-memory now, DB later)│
└────────────▲─────────────┘
             │ implements
┌────────────┴─────────────┐
│        Application        │
│   (use cases + ports)     │
└────────────▲─────────────┘
             │ uses
┌────────────┴─────────────┐
│          Domain           │
│  (pure business logic)    │
└──────────────────────────┘

```
*Key Rules:*

* Domain knows nothing about Spring, HTTP, or databases.

* Application orchestrates workflows but does not contain business rules.

* Infrastructure is an adapter, not the core.

```text
  
src/main/java/com/ermiyas/exchange
│
├── common/                 # Shared value objects
│   ├── Money.java          # Immutable, non-negative money
│   └── Odds.java           # Decimal odds (> 1.0)
│
├── domain/
│   ├── orderbook/          # Betting primitives
│   │   ├── Offer.java
│   │   └── BetAgreement.java
│   │
│   ├── wallet/             # Accounting & reservations
│   │   ├── Wallet.java
│   │   ├── WalletTransaction.java
│   │   └── InsufficientFundsException.java
│   │
│   └── settlement/         # Outcome modeling (WIP / evolving)
│
├── application/
│   ├── offer/              # Create / take offer use cases
│   └── settlement/         # Outcome settlement use case
│
├── infrastructure/
│   └── repository/         # In-memory implementations (temporary)
│
└── ExchangeApplication.java # Entry point (Spring Boot later)
```
📖 Suggested Reading OrderValue objects: Money.java, Odds.javaWallet & accounting: Wallet.java (Understand reservation vs available balance)Betting core: Offer.java, BetAgreement.javaUse cases: CreateOfferUseCase, TakeOfferUseCase, SettleOutcomeUseCaseInfrastructure: In-memory repositories (boring by design)⚖️ Key Domain Concepts💰 MoneyImmutable and non-negative.Explicit arithmetic (plus, minus, multiply).No raw BigDecimal leaks into business logic.📈 OddsDecimal odds (> 1.0).Profit part exposed via minusOne().📑 OfferRepresents a user’s intent to bet.Can be partially filled.Supports FOR / AGAINST positions.🤝 BetAgreementCreated when an offer is taken. It captures:Maker vs Taker.Asymmetric risk.Total payout and winner/loser derivation.🏦 Wallet (Reservation-based)Wallets do not immediately lose money when a bet is placed.Funds are reserved.Reservations are released at settlement.Winner is credited explicitly.Prevents double spending and inconsistent states.🔄 Betting & Settlement FlowUser A creates an offer.User B takes the offer.A BetAgreement is created.Both wallets reserve their respective risks.Event outcome is known.Settlement occurs: Reservations released, winner credited, agreement settled.🚧 Current StatusStatusFeature✅Core domain entities✅Reservation-based wallet model✅Offer creation & taking✅Basic settlement flow (stabilizing)🏗️Finalizing settlement logic consistency🏗️Improving test coverage📅Spring Boot adapters & REST APIs📅Persistence layer (JPA)🧪 What This Project Is NotNot a production-ready betting system.Not a legal or financial product.Not optimized for performance or scale (yet).Not a UI-first application.💬 Feedback & Suggestions WelcomeIf you are a student, developer, or reviewer, I’d genuinely appreciate feedback on:Domain modeling & Architecture choices.Naming & clarity.Edge cases I might be missing.Feel free to open issues or suggest improvements!
