package nbastats;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamStats {

    @Id
    @GeneratedValue
    private Long id;

//    @Transient
//    @Autowired
//    private TeamRepository teamRepository;

//    @OneToOne
//    private Game game;

    @OneToOne
    private Team team;
    @JsonUnwrapped
    @OneToOne(cascade = CascadeType.ALL)
    private Stats stats;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teamStats")
    private List<PlayerStats> playerStats;

    //    @JsonSetter("teamId")
    public void setTeamById(Long teamId, TeamRepository teamRepository) {
//        System.out.println(teamId);
//        System.out.println(teamRepository.findAll());
        team = teamRepository.findById(teamId);

    }

    /*
    @JsonCreator
    public TeamStats(
//            @JsonProperty("teamId") Long teamId,
            @JsonProperty("points") int points,
            @JsonProperty("totReb") int rebounds,
            @JsonProperty("assists") int assists) {
//        setTeamById(teamId);
        stats = new Stats(points, rebounds, assists);
    }
    */

    public TeamStats() {
    }

    public TeamStats(Long id, Stats stats, TeamRepository teamRepository) {
        setStats(stats);
        setTeamById(id, teamRepository);
    }

    public void addPlayerStats(PlayerStats playerStats) {
        if (this.playerStats == null)
            this.playerStats = new ArrayList<>();
        this.playerStats.add(playerStats);
    }

    public void addPlayerStats(List<PlayerStats> playerStats) {
        if (this.playerStats == null)
            this.playerStats = new ArrayList<>();
        this.playerStats.addAll(
                playerStats
                        .stream()
                        .filter(p -> p.getPlayer().getId().equals(team.getId()))
                        .collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "id=" + id +
                ", team=" + team +
                ", stats=" + stats +
                '}';
    }
}
