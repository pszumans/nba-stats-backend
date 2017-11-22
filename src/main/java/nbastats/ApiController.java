package nbastats;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final TeamRepository teamRepository;
    private final GameDateRepository gameDateRepository;

    public ApiController(TeamRepository teamRepository, GameDateRepository gameDateRepository) {
        this.teamRepository = teamRepository;
        this.gameDateRepository = gameDateRepository;
    }

    @GetMapping("/rosters")
    ResponseEntity<List<Team>> rosters() {
        return ResponseEntity.ok().body(teamRepository.findAll());
    }

    @GetMapping("/teams")
    ResponseEntity<List<Team>> teams() {
        return ResponseEntity.ok().body(teamRepository.findAll());
    }

    @GetMapping("/{date}")
    ResponseEntity<GameDate> games(@PathVariable String date) {
        return ResponseEntity.ok().body(gameDateRepository.findById(date));
    }
}
