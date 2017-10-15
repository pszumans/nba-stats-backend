package nbastats;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.JsonPath;

import javax.persistence.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @ManyToOne(cascade = CascadeType.ALL)
    private GameDate gameDate;

    @Id
    @JsonProperty("gameId")
    private String id;

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
    private List<TeamStats> teams;

    public void addPlayerStats(PlayerStats playerStats) {
        playerStats.generateStatsMap();
        if (playerStats.getPlayer() != null && playerStats.getPlayer().getTeam() != null) {
            Long id = playerStats.getPlayer().getTeam().getId();
            if (home.getTeam().getId().equals(id)) {
                playerStats.setTeamStats(home);
                home.addPlayerStats(playerStats);
            }
            else if (away.getTeam().getId().equals(id)) {
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
                ", id='" + id + '\'' +
                ", home=" + home +
                ", away=" + away +
                '}';
    }

    public void generateTeams() {
        teams = Arrays.asList(home, away);
    }
}
