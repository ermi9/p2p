
-- 1. USER & WALLET DOMAIN

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE wallets (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(id),
    total_balance NUMERIC(19,4) NOT NULL DEFAULT 0,
    reserved_balance NUMERIC(19,4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE wallet_transactions (
    id SERIAL PRIMARY KEY,
    wallet_id INT NOT NULL REFERENCES wallets(id),
    amount NUMERIC(19,4) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- 'DEPOSIT', 'RESERVE', 'CREDIT', 'DEBIT'
    ref_type VARCHAR(50),                  -- 'OFFER', 'AGREEMENT'
    ref_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- 2. EVENTS & FIXTURES 

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE, -- ID from the main Fixture Provider
    name VARCHAR(255) NOT NULL,      -- e.g., 'Arsenal vs Liverpool'
    start_time TIMESTAMP NOT NULL,
    league_code VARCHAR(10) NOT NULL, -- e.g., 'PL', 'PD'
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', 
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- 3. REFERENCE ODDS (The "Notebook" & Snapshots)


-- This is the "Notebook" that links event to the Odds Provider's ID
CREATE TABLE fixture_provider_mapping (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    provider_name VARCHAR(50) NOT NULL,      -- e.g., 'THE_ODDS_API'
    external_fixture_id VARCHAR(100) NOT NULL, -- The ID the Odds API uses
    UNIQUE(event_id, provider_name)
);

-- This stores the actual external prices for users to see
CREATE TABLE reference_odds_snapshots (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    provider_name VARCHAR(50) NOT NULL,
    outcome_name VARCHAR(50) NOT NULL, -- 'HOME_WIN', 'AWAY_WIN', 'DRAW'
    price NUMERIC(10,2) NOT NULL,
    fetched_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- 4. MARKETS & OUTCOMES (Internal Exchange)

CREATE TABLE markets (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL, -- e.g., 'Match Winner'
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE outcomes (
    id SERIAL PRIMARY KEY,
    market_id INT NOT NULL REFERENCES markets(id) ON DELETE CASCADE,
    external_id VARCHAR(100) UNIQUE, 
    name VARCHAR(100) NOT NULL,      -- e.g., 'Home', 'Away', 'Draw'
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- 5. OFFERS & ORDER BOOK (User Generated)

CREATE TABLE offers (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    outcome_id INT NOT NULL REFERENCES outcomes(id),
    odds NUMERIC(6,2) NOT NULL,
    initial_stake NUMERIC(19,4) NOT NULL,
    remaining_stake NUMERIC(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN', 
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


-- 6. MATCHING & SETTLEMENT

CREATE TABLE bet_agreements (
    id SERIAL PRIMARY KEY,
    offer_id INT NOT NULL REFERENCES offers(id),
    maker_user_id INT NOT NULL REFERENCES users(id),
    taker_user_id INT NOT NULL REFERENCES users(id),
    maker_risk NUMERIC(19,4) NOT NULL,
    taker_risk NUMERIC(19,4) NOT NULL,
    odds NUMERIC(6,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', 
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE event_results (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL UNIQUE REFERENCES events(id),
    winning_outcome_id INT NOT NULL REFERENCES outcomes(id),
    recorded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE settlements (
    id SERIAL PRIMARY KEY,
    agreement_id INT NOT NULL REFERENCES bet_agreements(id),
    winner_user_id INT NOT NULL REFERENCES users(id),
    payout_amount NUMERIC(19,4) NOT NULL,
    commission_paid NUMERIC(19,4) NOT NULL,
    settled_at TIMESTAMP NOT NULL DEFAULT NOW()
);