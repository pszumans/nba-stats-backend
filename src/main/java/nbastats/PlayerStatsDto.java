package nbastats;

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

    @JsonProperty("stats")
    private StatsDto statsDto;

    public PlayerStatsDto(PlayerStats playerStats) {
        this.teamStats = playerStats.getTeamStats();
        this.player = playerStats.getPlayer();
        this.statsDto = new StatsDto(playerStats.getStats());
    }
}
