package com.br.outsera.worstmovie.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "movie_year", nullable = false)
    private Integer year;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "studios")
    private String studios;
    
    @Column(name = "producers", nullable = false)
    private String producers;
    
    @Column(name = "winner", nullable = false)
    private Boolean winner = false;
}