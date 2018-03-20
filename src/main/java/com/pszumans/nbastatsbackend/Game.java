package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    @ManyToOne
    @JsonIgnore
    private GameDate gameDate;

    @Id
    @JsonProperty("gameId")
    private String gameId;

    @JsonProperty("isGameActivated")
    boolean isOnline;

    private String clock;

    @JsonProperty("period")
    @JsonDeserialize(using = QuarterDeserializer.class)
    private int quarter;

    @OneToOne(cascade = CascadeType.ALL)
    private TeamStats home;
    @OneToOne(cascade = CascadeType.ALL)
    private TeamStats away;

    @Transient
    @JsonIgnore
    private List<TeamStats> teams;

}
