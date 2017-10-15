package nbastats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.sun.istack.internal.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private String firstName;
    private String lastName;

    @JsonProperty("jersey")
    private int nr;

    @JsonProperty("pos")
    private String position;

    @Id
    @JsonProperty("personId")
    private Long id;

    @ManyToOne
    private Team team;

    @JsonSetter("teamId")
    public void setTeamById(Long teamId) {
        team = new Team(teamId);
    }
}
