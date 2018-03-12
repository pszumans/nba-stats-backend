package nbastats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findAllByGameDateId(String gameDateId);
    @Query("select g.isOnline from Game g where g.gameId = gameId ")
    boolean existsByIdAndIsOnlineTrue(String gameId);
}
