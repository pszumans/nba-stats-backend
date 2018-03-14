package com.pszumans.nbastatsbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface GameDateRepository extends JpaRepository<GameDate, Date> {
    GameDate findById(String id);
    boolean existsById(String date);
}
