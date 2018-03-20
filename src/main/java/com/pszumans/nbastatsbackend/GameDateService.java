package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

@Slf4j
@Service
public class GameDateService {

    @Value("${nba.data}")
    private String NBA_DATA;// = "http://data.nba.com/10s/prod/v1/";

    private final GameDateRepository gameDateRepository;
    private final GameRepository gameRepository;
    private final GameDateConverter gameDateConverter;

    public GameDateService(GameDateRepository gameDateRepository, GameRepository gameRepository, GameDateConverter gameDateConverter) {
        this.gameRepository = gameRepository;
        this.gameDateRepository = gameDateRepository;
        this.gameDateConverter = gameDateConverter;
    }

    public GameDate serveGameDate(String date) {
        if (gameDateRepository.existsById(date) || gameDateConverter.allGamesFinished(date)) {
            return gameDateRepository.findById(date).orElseGet(
                    () -> gameDateConverter.convertGameDate(date, saveGameDateFunction(), saveGameFunction())
            );
        }
        return gameDateConverter.convertGameDate(date);
    }

    public Game serveGame(String date, String gameId) {
        if (gameRepository.existsById(gameId) || gameDateConverter.hasGameFinished(date, gameId)) {
            return gameRepository.findById(gameId).orElseGet(
                    () -> gameDateConverter.convertGame(date, gameId)
            );
        }
        return gameDateConverter.convertGame(date, gameId);
    }

    private Function<Game, Game> saveGameFunction() {
        return game -> gameRepository.save(game);
    }

    private Function<GameDate, GameDate> saveGameDateFunction() {
        return gameDate -> gameDateRepository.save(gameDate);
    }

}
