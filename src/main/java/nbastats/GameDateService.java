package nbastats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class GameDateService {

    @Value("${nba.data}")
    private String NBA_DATA;// = "http://data.nba.com/10s/prod/v1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    private  GameDateRepository gameDateRepository;

    public GameDateService(RestTemplate restTemplate, ObjectMapper mapper,
                           GameRepository gameRepository, TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public void consumeStats(GameDate gameDate) {
        String dataUrl = NBA_DATA + gameDate.getId() + "/";
        String scoreBoardUrl = dataUrl + "scoreboard.json";
        log.info(scoreBoardUrl);
        JsonNode gameNode = restTemplate.getForObject(dataUrl + "scoreboard.json", JsonNode.class).path("games");
        List<Game> games = mapper.convertValue(gameNode, new TypeReference<List<Game>>() {});
        consumeGamesStats(dataUrl, games, gameDate);
        List<Game> gameList = gameRepository.findAllByGameDateId(gameDate.getId());
        gameDate.setGames(gameList);
        gameList.forEach(g -> g.setGameDate(gameDate));
        System.out.println(gameDateRepository.findAll());
    }

    private void consumeGamesStats(String data, List<Game> games, GameDate gameDate) {
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (!checkGame(game)) {
                consumeGame(data, gameDate, i, game);
            }
        }
    }

    private void consumeGame(String data, GameDate gameDate, int gameIt, Game game) {
        game.setPeriod(restTemplate.getForObject(
                data + "scoreboard.json", JsonNode.class).path("games").path(gameIt).path("period").get("current").asInt());
        String boxscoreUrl = data + game.getGameId() + "_boxscore.json";
        log.info(boxscoreUrl);
        JsonNode jsonStats = restTemplate.getForObject(data + game.getGameId() + "_boxscore.json", JsonNode.class);
        Arrays.asList("vTeam", "hTeam").forEach(team -> setTeamStats(game, jsonStats, team));
//        game.generateTeams();
        List<PlayerStats> playerStats = mapper.convertValue(jsonStats.at("/stats/activePlayers"), new TypeReference<List<PlayerStats>>() {
        });
        if (playerStats != null)
            IntStream.range(0, playerStats.size()).forEach(i -> {
                PlayerStats playerStat = playerStats.get(i);
                consumePlayer(game, jsonStats, i, playerStat);
            });
        game.setGameDate(gameDate);
        gameRepository.save(game);
    }

    private void consumePlayer(Game game, JsonNode jsonStats, int playerStatsIt, PlayerStats playerStats) {
        Long id = jsonStats.at("/stats/activePlayers").path(playerStatsIt).get("personId").asLong();
        Player player = playerRepository.findByPlayerId(id);
        playerStats.setPlayer(player);
        game.addPlayerStats(playerStats);
    }

    private boolean checkGame(Game game) {
        return !gameRepository.existsById(game.getGameId()) || gameRepository.existsByIdAndIsOnlineTrue(game.getGameId());
    }

    private void setTeamStats(Game game, JsonNode jsonStats, String teamName) {
        Long teamId = jsonStats.path("basicGameData").path(teamName).path("teamId").asLong();
        Stats stats = mapper.convertValue(jsonStats.path("stats").path(teamName).path("totals"), Stats.class);
        Team team = teamRepository.findByTeamId(teamId);
        TeamStats teamStats = new TeamStats(stats, team);
        if (teamName.equals("vTeam")) {
            game.setAway(teamStats);
        } else if (teamName.equals("hTeam")) {
            game.setHome(teamStats);
        }
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<Game> getGameDate(String date) {
        return gameRepository.findAllByGameDateId(date);
    }

    public GameDate consumeStats(String date) throws ParseException {
        if (!gameDateRepository.existsById(date)) {
            consumeStats(new GameDate(date));
        }
        return gameDateRepository.findById(date);
    }
}
