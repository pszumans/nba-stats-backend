package nbastats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner init(RestTemplate restTemplate, PlayerRepository playerRepository, TeamRepository teamRepository) {
        return (args) -> {
            ObjectMapper mapper = new ObjectMapper();
            mapTeams(restTemplate, teamRepository, mapper);
            List<Player> players = mapPlayers(restTemplate, mapper);
            joinPlayers(playerRepository, teamRepository, players);
        };
    }

    private void joinPlayers(PlayerRepository playerRepository, TeamRepository teamRepository, List<Player> players) {
        players.forEach(p -> {
            Team team = teamRepository.findById(p.getTeam().getId());
            p.setTeam(team);
            playerRepository.save(p);
        });
    }

    private List<Player> mapPlayers(RestTemplate restTemplate, ObjectMapper mapper) {
        JsonNode playersNode = restTemplate.getForObject(
                "http://data.nba.com/10s/prod/v1/2017/players.json", JsonNode.class)
                .at("/league/standard");
        return mapper.convertValue(playersNode, new TypeReference<List<Player>>(){});
    }

    private void mapTeams(RestTemplate restTemplate, TeamRepository teamRepository, ObjectMapper mapper) {
        JsonNode teamsNode = restTemplate.getForObject(
                "http://data.nba.com/10s/prod/v1/2017/teams.json", JsonNode.class)
                .at("/league/standard");
        List<Team> teams = mapper.convertValue(teamsNode, new TypeReference<List<Team>>(){});
        teams.forEach(t -> teamRepository.save(t));
    }
}