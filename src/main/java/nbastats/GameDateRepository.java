package nbastats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface GameDateRepository extends JpaRepository<GameDate, Date> {
    boolean existsById(String id);
    GameDate findById(String id);
}
