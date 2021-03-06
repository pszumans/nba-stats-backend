package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Team {

    @JsonProperty("fullName")
    private String name;

    @Id
    @JsonProperty("teamId")
    private Long teamId;

    private String urlName;

    private String tricode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private List<Player> players;

    public Team(Long teamId) {
        this.teamId = teamId;
    }
}
