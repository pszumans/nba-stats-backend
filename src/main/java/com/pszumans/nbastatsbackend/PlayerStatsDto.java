package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PlayerStatsDto {

    @JsonIgnore
    private TeamStats teamStats;

    @JsonProperty("info")
    private Player player;

    private boolean isOnCourt;

    @JsonProperty("stats")
    private StatsDto statsDto;

    public PlayerStatsDto(PlayerStats playerStats) {
        this.teamStats = playerStats.getTeamStats();
        this.player = playerStats.getPlayer();
        this.isOnCourt = playerStats.isOnCourt();
        this.statsDto = StatsDto.convertToDto(playerStats.getStats());
    }

    public static PlayerStatsDto convertToDto(PlayerStats playerStats) {
        return playerStats == null ? null : new PlayerStatsDto(playerStats);
    }
}
