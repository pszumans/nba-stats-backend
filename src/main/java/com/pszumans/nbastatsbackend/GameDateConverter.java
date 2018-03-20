package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class GameDateConverter {

    @Value("${nba.data}")
    private String NBA_DATA;// = "http://data.nba.com/10s/prod/v1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    private JsonNode boxscoreNode;

    public GameDateConverter(RestTemplate restTemplate, ObjectMapper objectMapper, TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    private JsonNode parseBoxscoreNode(String date, String gameId) {
        if (boxscoreNode == null) {
            String boxscoreUrl = parseBoxscoreUrl(date, gameId);
            log.info(boxscoreUrl);
            boxscoreNode = restTemplate.getForObject(boxscoreUrl, JsonNode.class);
        }
        return boxscoreNode;
    }

    private String parseBoxscoreUrl(String date, String gameId) {
        return parseDataUrl(date) + gameId + "_boxscore.json";
    }

    private String parseDataUrl(String date) {
        return NBA_DATA + date + "/";
    }

    private String parseScoreboardUrl(String date) {
        return parseDataUrl(date) + "scoreboard.json";
    }

    public GameDate convertGameDate(String date) {
        return convertGameDate(date, Function.identity(), Function.identity());
    }

    public GameDate convertGameDate(String date, Function<GameDate, GameDate> gameDateFunction, Function<Game, Game> gameFunction) {
        GameDate gameDate = gameDateFunction.apply(new GameDate(date));
        List<Game> games = parseGameIds(date)
                .stream()
                .map(gameId -> {
                    Game game = convertGame(date, gameId);
                    game.setGameDate(gameDate);
                    return gameFunction.apply(game);
                })
                .collect(Collectors.toList());
        gameDate.setGames(games);
        return gameDate;
    }

    public Game convertGame(String date, String gameId) {
        JsonNode gameNode = parseBoxscoreNode(date, gameId).get("basicGameData");
        Game game = objectMapper.convertValue(gameNode, Game.class);
        game.setHome(convertTeamStats(date, gameId, "hTeam"));
        game.setAway(convertTeamStats(date, gameId, "vTeam"));
        return game;
    }

    private TeamStats convertTeamStats(String date, String gameId, String teamSide) {
        JsonNode gameNode = parseBoxscoreNode(date, gameId);
        Stats stats = objectMapper.convertValue(gameNode.path("stats").path(teamSide).get("totals"), Stats.class);
        Long teamId = gameNode.path("basicGameData").path(teamSide).get("teamId").asLong();
        Team team = teamRepository.findById(teamId).get();
        TeamStats teamStats = new TeamStats(team, stats);
        teamStats.setPlayerStats(convertPlayerStats(date, gameId, teamSide));
        boxscoreNode = null;
        return teamStats;
    }

    private List<PlayerStats> convertPlayerStats(String date, String gameId, String teamSide) {
        JsonNode gameNode = parseBoxscoreNode(date, gameId);
        JsonNode playersNode = gameNode.at("/stats/activePlayers");
        List<PlayerStats> playerStatsList = objectMapper.convertValue(playersNode, new TypeReference<List<PlayerStats>>() {});
        long teamId = gameNode.path("basicGameData").path(teamSide).get("teamId").asLong();
        return IntStream.range(0, playerStatsList.size())
                .filter(i -> playersNode.path(i).get("teamId").asLong() == teamId)
                .mapToObj(i -> {
                    Player player = playerRepository.findById(playersNode.path(i).get("personId").asLong()).get();
                    PlayerStats playerStats = playerStatsList.get(i);
                    playerStats.setPlayer(player);
                    return playerStats;
                })
                .collect(Collectors.toList());
    }

    private List<String> parseGameIds(String date) {
        JsonNode scoreboardNode = parseGameNode(date);
        return IntStream.range(0, scoreboardNode.get("numGames").asInt())
                .mapToObj(i -> scoreboardNode.path("games").path(i).get("gameId").asText())
                .collect(Collectors.toList());
    }

    private JsonNode parseGameNode(String date) {
        String scoreboardUrl = parseScoreboardUrl(date);
        return restTemplate.getForObject(scoreboardUrl, JsonNode.class);
    }

    public boolean hasGameFinished(String date, String gameId) {
        JsonNode gameNode = parseBoxscoreNode(date, gameId);
        boolean isOnline = gameNode.get("isGameActivated").asBoolean();
        boolean hasStarted = gameNode.at("/period/current").asInt() != 0;
        return !isOnline && hasStarted;
    }

    public boolean allGamesFinished(String date) {
        JsonNode scoreboardNode = restTemplate.getForObject(parseScoreboardUrl(date), JsonNode.class);
        for (int i = 0; i < scoreboardNode.get("numGames").asInt(); i++) {
            boolean isOnline = scoreboardNode.path("games").get(i).get("isGameActivated").asBoolean();
            boolean hasStarted = scoreboardNode.path("games").get(i).at("/period/current").asInt() != 0;
            if (isOnline || !hasStarted) {
                return false;
            }
        }
        return true;
    }
}
