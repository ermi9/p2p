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
## 📖 Suggested Reading Order
If you’re new to the project, this order will save you time:

1.  **Value objects**
    * `Money.java`
    * `Odds.java`
2.  **Wallet & accounting**
    * `Wallet.java`
    * Understand reservation vs available balance
3.  **Betting core**
    * `Offer.java`
    * `BetAgreement.java`
4.  **Use cases**
    * `CreateOfferUseCase`
    * `TakeOfferUseCase`
    * `SettleOutcomeUseCase`
5.  **Infrastructure**
    * In-memory repositories (boring by design)

---

## ⚖️ Key Domain Concepts

### Money
* Immutable
* Non-negative
* Explicit arithmetic (`plus`, `minus`, `multiply`)
* No raw `BigDecimal` leaks into business logic

### Odds
* Decimal odds (> 1.0)
* Profit part exposed via `minusOne()`
* No bookmaker-style shortcuts

### Offer
* Represents a user’s intent to bet
* Can be partially filled
* Supports **FOR / AGAINST** positions
* Status is derived, not stored

### BetAgreement
Created when an offer (or part of it) is taken. It captures:
* Maker vs taker
* Asymmetric risk
* Total payout
* Winner / loser derivation based on outcome

### Wallet (Reservation-based)
Wallets do not immediately lose money when a bet is placed. Instead:
1. Funds are **reserved**
2. Reservations are **released** at settlement
3. Winner is **credited** explicitly

This prevents:
* Double spending
* Inconsistent states
* Hidden side effects

---

## 🔄 Betting & Settlement Flow (Conceptual)



1. **User A** creates an offer
2. **User B** takes the offer
3. A **BetAgreement** is created
4. Both wallets **reserve** their respective risks
5. Event outcome is known
6. **Settlement occurs:**
    * Reservations released
    * Winner credited
    * Agreement marked settled

Everything is explicit and traceable.

---

## 🚧 Current Status

### ✅ Implemented
* Core domain entities
* Reservation-based wallet model
* Offer creation & taking
* Basic settlement flow (still stabilizing)
* In-memory repositories
* Clean separation of layers

### 🏗️ In Progress
* Finalizing settlement logic consistency
* Removing legacy abstractions
* Hardening invariants
* Improving test coverage

### 📅 Planned (Coming Weeks)
* Freeze core domain
* Introduce Spring Boot adapters
* Persistence layer (JPA or similar)
* REST APIs
* Minimal frontend
* Documentation & examples

---

## 🧪 What This Project Is Not
* Not a production-ready betting system
* Not a legal or financial product
* Not optimized for performance or scale (yet)
* Not a UI-first application

*This is a learning vehicle, intentionally scoped.*

---

## 💬 Feedback & Suggestions Welcome
This project is actively evolving. If you are a student, developer, reviewer, or just curious, I’d genuinely appreciate feedback on:
* Domain modeling
* Architecture choices
* Naming
* Clarity
* Edge cases I might be missing

Feel free to open issues, leave comments, or suggest improvements.

📌 **Final Note:** The main intention behind this project is to learn how complex systems are built from the inside out, rather than relying on frameworks to hide the complexity. If you take the time to read through the core domain, thank you — and I’d love to hear your thoughts.
