package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect
public class GameDateDto {

    private String date;

    @JsonProperty("games")
    private List<GameDto> gamesDto;

    public GameDateDto(GameDate gameDate) {
        this.date = gameDate.getDateString();
        this.gamesDto = gameDate.getGames()
                .stream()
                .map(game -> new GameDto(game))
                .collect(Collectors.toList());
    }
}
