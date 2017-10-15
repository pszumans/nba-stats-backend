package nbastats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerStats {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private TeamStats teamStats;

    @OneToOne
    private Player player;
    @JsonUnwrapped
    @OneToOne(cascade = CascadeType.ALL)
    private Stats stats;

    public PlayerStats() {
    }

    public void setPlayerById(Long id, PlayerRepository playerRepository) {
        player = playerRepository.findById(id);
    }

    public void generateStatsMap() {
        stats.generateMap();
    }

}
