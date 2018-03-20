package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamStats {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JsonSerialize(using = TeamSerializer.class)
    private Team team;

    @JsonUnwrapped
    @OneToOne(cascade = CascadeType.ALL)
    private Stats stats;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teamStats")
    private List<PlayerStats> playerStats;

    public TeamStats(Team team, Stats stats) {
        this.team = team;
        this.stats = stats;
    }
}
