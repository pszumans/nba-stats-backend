package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect
public class GameDateDto {

    private static final Comparator<Game> GAME_COMPARATOR = new GameComparator();

    private String date;

    @JsonProperty("games")
    private List<GameDto> gamesDto;

    public GameDateDto(GameDate gameDate) {
        this.date = gameDate.getDateString();
        this.gamesDto = gameDate.getGames()
                .stream()
                .sorted(GAME_COMPARATOR)
                .map(game -> new GameDto(game))
                .collect(Collectors.toList());
    }
}
