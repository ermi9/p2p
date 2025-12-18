#🧩 P2P Sports Betting Exchange (Student Project)#
Overview

This repository contains a peer-to-peer (P2P) sports betting exchange built as a learning-focused student project, with a strong emphasis on core business logic, correctness, and architecture.

Unlike traditional bookmakers, this system allows users to bet against each other, not against the platform.

Users define their own odds

The platform takes no betting risk

Funds are handled via explicit reservation-based accounting

Settlement is deterministic and transparent

The primary goal of this project is not to ship a commercial product, but to deeply understand and implement the mechanics of an exchange-style betting system, step by step, starting from a clean, framework-agnostic core.

🎯 Project Goals

This project is intentionally designed to achieve the following:

1️⃣ Learn by building the core, not the shell

Most projects start with frameworks, databases, and UI.
This one starts with domain logic:

What does an offer really represent?

What happens when an offer is partially filled?

How do asymmetric risks work?

When does money actually move?

How do we prevent double-spending?

Only after these questions are answered does infrastructure come into play.

2️⃣ Model an exchange, not a bookmaker

Key characteristics of an exchange-style system:

Users bet FOR or AGAINST outcomes

Odds are user-defined

The platform does not set prices

The platform does not take risk

External bookmaker odds (future) are reference-only

This is closer to how real betting exchanges work under the hood.

3️⃣ Practice clean, evolvable architecture

The project follows a domain-first, clean architecture style:

Business rules are isolated

Use cases are explicit

Infrastructure is replaceable

Frameworks are delayed on purpose

This makes the system:

easier to reason about

easier to test

easier to evolve

🧠 High-level Architecture
Layered structure
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

Key rules

Domain knows nothing about Spring, HTTP, or databases

Application orchestrates workflows but does not contain business rules

Infrastructure is an adapter, not the core

📂 Project Structure (How to Navigate)
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

📖 Suggested Reading Order

If you’re new to the project, this order will save you time:

Value objects

Money.java

Odds.java

Wallet & accounting

Wallet.java

Understand reservation vs available balance

Betting core

Offer.java

BetAgreement.java

Use cases

CreateOfferUseCase

TakeOfferUseCase

SettleOutcomeUseCase

Infrastructure

In-memory repositories (boring by design)

⚖️ Key Domain Concepts
Money

Immutable

Non-negative

Explicit arithmetic (plus, minus, multiply)

No raw BigDecimal leaks into business logic

Odds

Decimal odds (> 1.0)

Profit part exposed via minusOne()

No bookmaker-style shortcuts

Offer

Represents a user’s intent to bet

Can be partially filled

Supports FOR / AGAINST positions

Status is derived, not stored

BetAgreement

Created when an offer (or part of it) is taken.

It captures:

maker vs taker

asymmetric risk

total payout

winner / loser derivation based on outcome

Wallet (Reservation-based)

Wallets do not immediately lose money when a bet is placed.

Instead:

Funds are reserved

Reservations are released at settlement

Winner is credited explicitly

This prevents:

double spending

inconsistent states

hidden side effects

🔄 Betting & Settlement Flow (Conceptual)

User A creates an offer

User B takes the offer

A BetAgreement is created

Both wallets reserve their respective risks

Event outcome is known

Settlement occurs:

reservations released

winner credited

agreement marked settled

Everything is explicit and traceable.

🚧 Current Status
Implemented

Core domain entities

Reservation-based wallet model

Offer creation & taking

Basic settlement flow (still stabilizing)

In-memory repositories

Clean separation of layers

In Progress

Finalizing settlement logic consistency

Removing legacy abstractions

Hardening invariants

Improving test coverage

Planned (Coming Weeks)

Freeze core domain

Introduce Spring Boot adapters

Persistence layer (JPA or similar)

REST APIs

Minimal frontend

Documentation & examples

🧪 What This Project Is Not

Not a production-ready betting system

Not a legal or financial product

Not optimized for performance or scale (yet)

Not a UI-first application

This is a learning vehicle, intentionally scoped.

💬 Feedback & Suggestions Welcome

This project is actively evolving.

If you are:

a student

a developer

a reviewer

or just curious

I’d genuinely appreciate feedback on:

domain modeling

architecture choices

naming

clarity

edge cases I might be missing

Feel free to open issues, leave comments, or suggest improvements.

📌 Final Note

The main intention behind this project is to learn how complex systems are built from the inside out, rather than relying on frameworks to hide the complexity.

If you take the time to read through the core domain, thank you — and I’d love to hear your thoughts.
