package nbastats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private GameDateRepository gameDateRepository;

    @GetMapping("/rosters")
    List<Team> teams() {
        return teamRepository.findAll();
    }

    @GetMapping("/{date}")
    GameDate games(@PathVariable String date) {
        return gameDateRepository.findById(date);
    }
}
