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
