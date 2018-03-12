package nbastats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    @JsonProperty("jersey")
    private int nr;

    @JsonProperty("pos")
    private String position;

    @Id
    @JsonProperty("personId")
    private Long playerId;

    private String firstName;
    private String lastName;

    @ManyToOne
    @JsonIgnore
    private Team team;

    @JsonSetter("teamId")
    public void setTeamById(Long teamId) {
        team = new Team(teamId);
    }
}
