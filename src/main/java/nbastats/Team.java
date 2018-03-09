package nbastats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
//    @JsonIgnore
    private List<Player> players;

    @JsonProperty("fullName")
    private String name;

    @Id
    @JsonProperty("teamId")
    private Long teamId;

    private String urlName;

    public Team(Long teamId) {
        this.teamId = teamId;
    }

    public void addPlayer(Player player) {
        if (players == null)
            players = new ArrayList<>();
        players.add(player);
    }

    public void addPlayers(List<Player> players) {
        if (players == null)
            players = new ArrayList<>();
        players.addAll(players);
    }

    public Team() {
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", teamId=" + teamId +
                ", urlName='" + urlName + '\'' +
                '}';
    }
}
