package nbastats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

@Controller
public class StatsController {

    private static final String NBA_DATA = "http://data.nba.com/10s/prod/v1/";

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final GameDateRepository gameDateRepository;
    private final GameRepository gameRepository;

    private ObjectMapper mapper;
    private RestTemplate restTemplate;

    private String getDateInLA() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return df.format(new Date());
    }

    @Autowired
    public StatsController(PlayerRepository playerRepository,
                           TeamRepository teamRepository,
                           GameDateRepository gameDateRepository,
                           GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.gameDateRepository = gameDateRepository;
        this.gameRepository = gameRepository;
    }

    @RequestMapping("/rosters")
    String rosters(Model model) {
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("teams", teams);
        return "rosters";
    }

    @RequestMapping("/teams")
    String teams() {
        return "teams";
    }

    @RequestMapping("boxscore/today")
    String today(Model model) throws ParseException {
        return "redirect:" + getDateInLA();
    }

    @RequestMapping("mboxscore/today")
    String todayM(Model model) throws ParseException {
        return "redirect:" + getDateInLA();
    }

    @RequestMapping("boxscore/{date}/mini")
        String boxscoreMini(@PathVariable String date, Model model) {
        return "redirect:/mboxscore/" + date;
    }

    @RequestMapping("/boxscore/{date}")
    String gameDate(@PathVariable String date, Model model) throws ParseException {
        GameDate gameDate = new GameDate(date);
//        if (gameDateRepository.existsById(gameDate.getId())) {
//            if (date.equals(getDateInLA())) {
//            } else {
//                gameDate = gameDateRepository.findById(gameDate.getId());
//                model.addAttribute("games", gameDate.getGames());
//            }
//        } else {
            consumeStats(model, gameDate);
            Map map = model.asMap();
            System.out.println("MAP #" + map.size() + " = " + map);
            model.addAttribute("mini", "/mboxscore/" + date);
//        }
        return "boxscore";
    }

    @RequestMapping("/mboxscore/{date}")
    String gameDateM(@PathVariable String date, Model model) throws ParseException {
        GameDate gameDate = new GameDate(date);
//        if (gameDateRepository.existsById(gameDate.getId())) {
//            if (date.equals(getDateInLA())) {
//
//            } else {
//                gameDate = gameDateRepository.findById(gameDate.getId());
//                model.addAttribute("games", gameDate.getGames());
//            }
//        } else {
        consumeStats(model, gameDate);
        Map map = model.asMap();
        System.out.println("MAP #" + map.size() + " = " + map);
//        }
        return "mboxscore";
    }

    private void consumeStats(Model model, GameDate gameDate) {
        initMappers();
        String data = NBA_DATA + gameDate.getId() + "/";
        System.out.println(data + "scoreboard.json");
        JsonNode gameNode = restTemplate.getForObject(
                data + "scoreboard.json", JsonNode.class)
                .path("games");
        List<Game> games = mapper.convertValue(gameNode, new TypeReference<List<Game>>() {
        });
        consumeGamesStats(data, games, gameDate);
        List<Game> gameList = gameRepository.findAllByGameDateId(gameDate.getId());
        gameDate.setGames(gameList);
        gameList.forEach(g -> g.setGameDate(gameDate));
        model.addAttribute("games", gameDate.getGames());
        model.addAttribute("keys", Stats.KEYS);
    }

    private void initMappers() {
        if (restTemplate == null)
            restTemplate = new RestTemplate();
        if (mapper == null)
            mapper = new ObjectMapper();
    }

    private void consumeGamesStats(String data, List<Game> games, GameDate gameDate) {
        Iterator<Integer> it = IntStream.range(0, games.size()).iterator();
        games.forEach(g -> {
            g.setPeriod(restTemplate.getForObject(
                    data + "scoreboard.json", JsonNode.class)
                    .path("games")
                    .path(it.next())
                    .path("period")
                    .get("current").asInt());
            System.out.println(data + g.getId() + "_boxscore.json");
            JsonNode stats = restTemplate.getForObject(data + g.getId() + "_boxscore.json", JsonNode.class);
            Arrays.asList("vTeam", "hTeam").forEach(t -> {
                Long teamId = stats.path("basicGameData").path(t).path("teamId").asLong();
                Stats teamStats = mapper.convertValue(stats.path("stats").path(t).path("totals"), Stats.class);
                TeamStats team = new TeamStats(teamId, teamStats, teamRepository);
                if (t.equals("vTeam"))
                    g.setAway(team);
                else if (t.equals("hTeam"))
                    g.setHome(team);
            }
            );
            g.generateTeams();
            System.out.println("TEAMS = " + g.getTeams());
            List<PlayerStats> playerStats = mapper.convertValue(stats.at("/stats/activePlayers"), new TypeReference<List<PlayerStats>>(){});
            if (playerStats != null)
                IntStream.range(0, playerStats.size()).forEach(i -> {
                Long id = stats.at("/stats/activePlayers").path(i).get("personId").asLong();
                playerStats.get(i).setPlayerById(id, playerRepository);
                g.addPlayerStats(playerStats.get(i));
            });
            g.setGameDate(gameDate);
            gameRepository.save(g);
        });
    }

}