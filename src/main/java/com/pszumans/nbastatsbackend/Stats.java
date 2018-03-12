package nbastats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    @Id
    @GeneratedValue
    private Long id;

    private String min;

    private int points;
    @JsonProperty("totReb")
    private int rebounds;
    private int assists;

    private int steals;
    private int blocks;

    private int fgm;
    private int fga;
    private double fgp;

    private int tpm;
    private int tpa;
    private double tpp;

    private int ftm;
    private int fta;
    private double ftp;

    private int offReb;
    private int defReb;

    private int turnovers;
    @JsonProperty("pFouls")
    private int fouls;

    private int plusMinus;

    public static final String[] KEYS = {
            "MIN",
            "PTS", "REB", "AST", "STL", "BLK",
            "FGA", "FGM", "FG%",
            "3GA", "3GM", "3G%",
            "FTA", "FTM", "FT%",
            "OREB", "DREB",
            "TOV", "PF",
            "+/-",

    };

    @ElementCollection
    private List<String> map;

    public Stats() {}

    public void generateMap() {
        map = new ArrayList<>();
        String[] values = values();
        for (int i = 0; i < KEYS.length; i++) {
            map.add(values[i]);
        }
    }

    private String[] values() {
        return new String[] {
                min + "",
                points + "",
                rebounds + "",
                assists + "",
                steals + "",
                blocks + "",
                fgm + "",
                fga + "",
                fgp + "",
                tpm + "",
                tpa + "",
                tpp + "",
                ftm + "",
                fta + "",
                ftp + "",
                offReb + "",
                defReb + "",
                turnovers + "",
                fouls + "",
                plusMinus + "",
        };
    }
}
