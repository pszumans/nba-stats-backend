package nbastats;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ApiController {

    private final TeamRepository teamRepository;
    private final GameDateService gameDateService;

    public ApiController(TeamRepository teamRepository, GameDateService gameDateService) {
        this.teamRepository = teamRepository;
        this.gameDateService = gameDateService;
    }

    @GetMapping("/rosters")
    public ResponseEntity<List<Team>> rosters() {
        return ResponseEntity.ok().body(teamRepository.findAll());
    }

    @GetMapping("/boxscore/{date}")
    public ResponseEntity<GameDateDto> games(@PathVariable String date) throws ParseException {
        GameDate gameDate = gameDateService.consumeStats(date);
        return ResponseEntity.ok().body(new GameDateDto(gameDate));
    }
}
