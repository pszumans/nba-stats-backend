package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TeamStatsDto {

    @JsonSerialize(using = TeamSerializer.class)
//    @JsonUnwrapped
//    @JsonIgnore
    private Team team;

//    @JsonUnwrapped
    @JsonProperty("stats")
    private StatsDto statsDto;

    @JsonProperty("players")
    private List<PlayerStatsDto> playerStatsDto;

    public TeamStatsDto(TeamStats teamStats) {
        this.team = teamStats.getTeam();
        this.statsDto = StatsDto.convertToDto(teamStats.getStats());
        playerStatsDto = convertPlayerStatsToDto(teamStats);
    }

    private List<PlayerStatsDto> convertPlayerStatsToDto(TeamStats teamStats) {
        return teamStats.getPlayerStats() == null ? Collections.emptyList()
                : teamStats
                .getPlayerStats()
                .stream()
                .map(ps -> PlayerStatsDto.convertToDto(ps))
                .collect(Collectors.toList());
    }
}
