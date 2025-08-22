package com.br.outsera.worstmovie.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.br.outsera.worstmovie.entity.Movie;
import com.br.outsera.worstmovie.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes CSV Reader")
public class CSVReaderTest {
	
	@Mock
    private MovieRepository movieRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private CSVReader csvReader;

    @Captor
    private ArgumentCaptor<List<Movie>> movieListCaptor;

    @Test
    @DisplayName("Teste 1: Não deve carregar filmes quando repositório já tem dados")
    void shouldNotLoadMoviesWhenRepositoryHasData() throws Exception {
        when(movieRepository.count()).thenReturn(100L);

        csvReader.run(applicationArguments);

        verify(movieRepository).count();
        verify(movieRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Teste 2: Carrega vencedores e não-vencedores")
    void shouldLoadWinnersAndNonWinners() throws Exception {
        when(movieRepository.count()).thenReturn(0L);

        csvReader.run(applicationArguments);

        verify(movieRepository).saveAll(movieListCaptor.capture());

        List<Movie> savedMovies = movieListCaptor.getValue();
        
        long winnersCount = savedMovies.stream()
        		.filter(Movie::getWinner)
        		.count();
        
        long nonWinnersCount = savedMovies.stream()
        		.filter(movie -> !movie.getWinner())
        		.count();
        
        
        assertTrue(winnersCount > 0, "Deve ter filmes vencedores");
        assertTrue(nonWinnersCount > 0, "Deve ter filmes não-vencedores");
    }


    @Test
    @DisplayName("Teste 3: Tratamento de erro")
    void shouldHandleError() throws Exception {
        when(movieRepository.count()).thenReturn(0L);
        when(movieRepository.saveAll(any())).thenThrow(new RuntimeException("Erro no banco"));

        assertDoesNotThrow(() -> csvReader.run(applicationArguments));
        
        verify(movieRepository).count();
        verify(movieRepository).saveAll(any());
    }


    @Test
    @DisplayName("Teste 41: Deve carregar filmes conhecidos do CSV")
    void shouldLoadKnownMoviesFromCsv() throws Exception {
    	
        when(movieRepository.count()).thenReturn(0L);

        csvReader.run(applicationArguments);

        verify(movieRepository).saveAll(movieListCaptor.capture());
        
        List<Movie> savedMovies = movieListCaptor.getValue();
        
        // Verificar alguns filmes conhecidos do CSV
        String[] knownMovies = {
            "Can't Stop the Music",
            "Mommie Dearest", 
            "Cats",
            "The Emoji Movie"
        };
        
        for (String movieTitle : knownMovies) {
            boolean movieExists = savedMovies.stream()
                .anyMatch(movie -> movieTitle.equals(movie.getTitle()));
            
            assertTrue(movieExists, "Filme '" + movieTitle + "' deve estar no CSV");
        }
    }

}
