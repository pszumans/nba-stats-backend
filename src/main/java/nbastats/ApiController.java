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
    private final GameDateRepository gameDateRepository;
    private final GameDateService gameDateService;

    public ApiController(TeamRepository teamRepository, GameDateRepository gameDateRepository, GameDateService gameDateService) {
        this.teamRepository = teamRepository;
        this.gameDateRepository = gameDateRepository;
        this.gameDateService = gameDateService;
    }

    @GetMapping("/rosters")
    public ResponseEntity<List<Team>> rosters() {
        return ResponseEntity.ok().body(teamRepository.findAll());
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> teams() {
        return ResponseEntity.ok().body(teamRepository.findAll());
    }

    @GetMapping("/{date}")
    public ResponseEntity<GameDateDto> games(@PathVariable String date) throws ParseException {
        GameDate gameDate = gameDateService.consumeStats(date);
        System.out.println("api: " + gameDateRepository.findAll());
        return ResponseEntity.ok().body(new GameDateDto(gameDate));
    }
}
