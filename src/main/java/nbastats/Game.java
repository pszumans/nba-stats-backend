package nbastats;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private GameDate gameDate;

    @Id
    @JsonProperty("gameId")
    private String gameId;

    @JsonProperty("isGameActivated")
    boolean isOnline;

    private String clock;

    @JsonIgnore
    private int period;

    @OneToOne(cascade = CascadeType.ALL)
    private TeamStats home;
    @OneToOne(cascade = CascadeType.ALL)
    private TeamStats away;

    @Transient
    @JsonIgnore
    private List<TeamStats> teams;

    public void addPlayerStats(PlayerStats playerStats) {
//        playerStats.generateStatsMap();
        if (playerStats.getPlayer() != null && playerStats.getPlayer().getTeam() != null) {
            Long id = playerStats.getPlayer().getTeam().getTeamId();
            if (home.getTeam().getTeamId().equals(id)) {
                playerStats.setTeamStats(home);
                home.addPlayerStats(playerStats);
            }
            else if (away.getTeam().getTeamId().equals(id)) {
                playerStats.setTeamStats(away);
                away.addPlayerStats(playerStats);
            }
        }
    }

    public void addPlayerStats(List<PlayerStats> playerStats) {
        playerStats.forEach(p -> {
            addPlayerStats(p);
        });
    }

    @Override
    public String toString() {
        return "Game{" +
                ", gameDateId=" + gameDate.getId() +
                ", playerId='" + gameId + '\'' +
                ", home=" + home +
                ", away=" + away +
                '}';
    }

//    public void generateTeams() {
//        teams = Arrays.asList(home, away);
//    }

}
