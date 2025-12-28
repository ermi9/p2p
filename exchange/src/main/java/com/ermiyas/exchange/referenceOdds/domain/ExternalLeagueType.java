package com.ermiyas.exchange.referenceOdds.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
//league codes for the api i used to fetch the odds
//couldn't use the league.java because the league codes were different in these two apis
@Getter
@RequiredArgsConstructor
public enum ExternalLeagueType {
    PREMIER_LEAGUE("soccer_epl"),
    LA_LIGA("soccer_spain_la_liga"),
    BUNDESLIGA("soccer_germany_bundesliga"),
    SERIE_A("soccer_italy_serie_a"),
    LIGUE_1("soccer_france_ligue_1"),
    CHAMPIONS_LEAGUE("soccer_uefa_champions_league");

    private final String apiCode;
}