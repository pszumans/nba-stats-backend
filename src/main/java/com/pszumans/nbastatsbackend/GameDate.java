package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class GameDate {

    @Id
    private String id;

    @JsonIgnore
    private Date date;

    @OneToMany(mappedBy = "gameDate", cascade = CascadeType.ALL)
    private List<Game> games;


    public void setDateById(String dateId) {
        try {
            this.date = new SimpleDateFormat("yyyyMMdd").parse(dateId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @JsonProperty("date")
    public String getDateString() {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public GameDate(String id) {
        this.id = id;
        setDateById(id);
    }

    public GameDate(Date date) {
        this.date = date;
        this.id = getDateString();
    }

    public void addGame(Game game) {
        if (games == null) {
            games = new ArrayList<>();
        }
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getGameId().equals(game.getGameId())) {
                games.set(i, game);
                return;
            }
        }
        games.add(game);
    }
}
