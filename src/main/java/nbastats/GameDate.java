package nbastats;

import lombok.Data;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class GameDate {

    @Id
    private String id;

    private Date date;

    @OneToMany(mappedBy = "gameDate")
    private List<Game> games;

    public GameDate() {
    }

    public void setDateById(String dateId) throws ParseException {
        this.date = new SimpleDateFormat("yyyyMMdd").parse(dateId);
    }

    public String getDateString() {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public GameDate(String id) throws ParseException {
        this.id = id;
        setDateById(id);
    }

    public GameDate(Date date) {
        this.date = date;
        this.id = getDateString();
    }
}
