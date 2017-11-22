package nbastats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class GameDateService {

    @Value("${nba.data}")
    private String NBA_DATA;// = "http://data.nba.com/10s/prod/v1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public GameDateService(RestTemplate restTemplate, ObjectMapper mapper,
                           GameRepository gameRepository, TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public void consumeStats(Model model, GameDate gameDate) {
        String data = NBA_DATA + gameDate.getId() + "/";
        System.out.println(data + "scoreboard.json");
        JsonNode gameNode = restTemplate.getForObject(data + "scoreboard.json", JsonNode.class).path("games");
        List<Game> games = mapper.convertValue(gameNode, new TypeReference<List<Game>>() {
        });
        consumeGamesStats(data, games, gameDate);
        List<Game> gameList = gameRepository.findAllByGameDateId(gameDate.getId());
        gameDate.setGames(gameList);
        gameList.forEach(g -> g.setGameDate(gameDate));
        model.addAttribute("games", gameDate.getGames());
        model.addAttribute("keys", Stats.KEYS);
    }

    private void consumeGamesStats(String data, List<Game> games, GameDate gameDate) {
        Iterator<Integer> it = IntStream.range(0, games.size()).iterator();
        games.forEach(game -> {
            game.setPeriod(restTemplate.getForObject(
                    data + "scoreboard.json", JsonNode.class).path("games").path(it.next()).path("period").get("current").asInt());
            System.out.println(data + game.getId() + "_boxscore.json");
            JsonNode jsonStats = restTemplate.getForObject(data + game.getId() + "_boxscore.json", JsonNode.class);
            Arrays.asList("vTeam", "hTeam").forEach(team -> {
                        setTeamStats(game, jsonStats, team);
                    }
            );
            game.generateTeams();
            System.out.println("TEAMS = " + game.getTeams());
            List<PlayerStats> playerStats = mapper.convertValue(jsonStats.at("/stats/activePlayers"), new TypeReference<List<PlayerStats>>() {
            });
            if (playerStats != null)
                IntStream.range(0, playerStats.size()).forEach(i -> {
                    Long id = jsonStats.at("/stats/activePlayers").path(i).get("personId").asLong();
                    Player player = playerRepository.findById(id);
                    playerStats.get(i).setPlayer(player);
                    game.addPlayerStats(playerStats.get(i));
                });
            game.setGameDate(gameDate);
            gameRepository.save(game);
        });
    }

    private void setTeamStats(Game game, JsonNode jsonStats, String teamName) {
        Long teamId = jsonStats.path("basicGameData").path(teamName).path("teamId").asLong();
        Stats stats = mapper.convertValue(jsonStats.path("stats").path(teamName).path("totals"), Stats.class);
        Team team = teamRepository.findById(teamId);
        TeamStats teamStats = new TeamStats(stats, team);
        if (teamName.equals("vTeam"))
            game.setAway(teamStats);
        else if (teamName.equals("hTeam"))
            game.setHome(teamStats);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<Game> getGameDate(String date) {
        return gameRepository.findAllByGameDateId(date);
    }
}
