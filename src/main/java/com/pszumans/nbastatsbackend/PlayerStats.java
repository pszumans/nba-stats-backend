package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerStats {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnore
    private TeamStats teamStats;

    @OneToOne
    private Player player;

    @JsonProperty
    private boolean isOnCourt;

    @JsonUnwrapped
    @OneToOne(cascade = CascadeType.ALL)
    private Stats stats;

}
