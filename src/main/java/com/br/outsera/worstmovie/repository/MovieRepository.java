package com.br.outsera.worstmovie.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.br.outsera.worstmovie.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>  {

    @Query("SELECT m FROM Movie m WHERE m.winner = true ORDER BY m.year")
    List<Movie> findAllWinners();
    
    @Query("SELECT DISTINCT m.producers FROM Movie m WHERE m.winner = true")
    List<String> findDistinctWinningProducers();
}
