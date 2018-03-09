package nbastats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findAllByGameDateId(String gameDateId);
    Game findByGameId(String id);
    boolean existsByIdAndIsOnlineTrue(String gameId);
}
