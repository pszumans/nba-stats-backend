package nbastats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
public class StatsController {

    private final GameDateService gameDateService;

    private Link link;

    private String getDateInLA() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return df.format(new Date());
    }

    @Autowired
    public StatsController(GameDateService gameDateService) {
        this.gameDateService = gameDateService;
    }

    @RequestMapping("/rosters")
    String rosters(Model model) {
        List<Team> teams = gameDateService.getAllTeams();
        model.addAttribute("teams", teams);
        return "rosters";
    }

    @RequestMapping("/today")
    String gd(Model model) {
        model.addAttribute("api", "/api/" + getDateInLA());
        return "box";
    }
    
    @RequestMapping("/link")
    ResponseEntity link() {
        ResourceSupport resourceSupport = new ResourceSupport();
        resourceSupport.add(link);
        return ResponseEntity.ok().body(link);
    }

    @RequestMapping("boxscore/today")
    String today() {
        return "redirect:" + getDateInLA();
    }

    @RequestMapping("mboxscore/today")
    String todayM() {
        return "redirect:" + getDateInLA();
    }

    @RequestMapping("boxscore/{date}/mini")
    String boxscoreMini(@PathVariable String date) {
        return "redirect:/mboxscore/" + date;
    }

    @RequestMapping("/boxscore/{date}")
    String gameDate(@PathVariable String date, Model model) throws ParseException {
        link = linkTo(methodOn(ApiController.class).games(date)).withSelfRel();//.withRel("link");
        GameDate gameDate = new GameDate(date);
        gameDateService.consumeStats(gameDate);
        return "boxscore";
    }

    @RequestMapping("/mboxscore/{date}")
    String gameDateM(@PathVariable String date, Model model) throws ParseException {
        GameDate gameDate = new GameDate(date);
        gameDateService.consumeStats(gameDate);
        return "mboxscore";
    }

    @GetMapping("/index")
    String index() {
        return "index";
    }
}