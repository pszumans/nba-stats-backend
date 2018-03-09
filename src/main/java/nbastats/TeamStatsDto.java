package nbastats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TeamStatsDto {

    @JsonSerialize(using = TeamSerializer.class)
//    @JsonUnwrapped
//    @JsonIgnore
    private Team team;

    @JsonUnwrapped
    @JsonIgnore
    private StatsDto statsDto;

    @JsonProperty("players")
    private List<PlayerStatsDto> playerStatsDto;

    public TeamStatsDto(TeamStats teamStats) {
        this.team = teamStats.getTeam();
        this.statsDto = new StatsDto(teamStats.getStats());
        playerStatsDto = teamStats
                .getPlayerStats()
                .stream()
                .map(ps -> new PlayerStatsDto(ps))
                .collect(Collectors.toList());
    }
}
