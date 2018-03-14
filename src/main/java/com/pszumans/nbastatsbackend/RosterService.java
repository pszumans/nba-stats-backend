package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RosterService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public RosterService(RestTemplate restTemplate, ObjectMapper mapper, TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public void setRosters() {
        mapTeams();
        List<Player> players = mapPlayers();
        joinPlayers(players);
    }

    private void joinPlayers(List<Player> players) {
        players.forEach(p -> {
            Team team = teamRepository.findByTeamId(p.getTeam().getTeamId());
            p.setTeam(team);
            playerRepository.save(p);
        });
    }

    private List<Player> mapPlayers() {
        JsonNode playersNode = restTemplate.getForObject(
                "http://data.nba.com/10s/prod/v1/2017/players.json", JsonNode.class)
                .at("/league/standard");
        return mapper.convertValue(playersNode, new TypeReference<List<Player>>(){});
    }

    private void mapTeams() {
        JsonNode teamsNode = restTemplate.getForObject(
                "http://data.nba.com/10s/prod/v1/2017/teams.json", JsonNode.class)
                .at("/league/standard");
        List<Team> teams = new ArrayList<>();
        for (JsonNode teamNode : teamsNode) {
            if (teamNode.get("isNBAFranchise").asBoolean() && !teamRepository.existsById(teamNode.get("teamId").asLong())) {
                teams.add(mapper.convertValue(teamNode, Team.class));
            }
        }
        teamRepository.saveAll(teams);
    }
}
