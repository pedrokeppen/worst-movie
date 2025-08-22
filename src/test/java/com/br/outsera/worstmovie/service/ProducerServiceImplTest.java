package com.br.outsera.worstmovie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.br.outsera.worstmovie.entity.Movie;
import com.br.outsera.worstmovie.repository.MovieRepository;
import com.br.outsera.worstmovie.service.impl.ProducerServiceImpl;

import io.swagger.model.AwardInterval;
import io.swagger.model.ProducerInterval;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Serviço")
public class ProducerServiceImplTest {

	  @Mock
	    private MovieRepository movieRepository;

	    @InjectMocks
	    private ProducerServiceImpl producerService;

	    @Test
	    @DisplayName("Teste 1: Deve calcular intervalo mínimo de 1 ano corretamente")
	    void shouldCalculateMinimumIntervalOf1YearCorrectly() {
	        // Produtor com vitórias consecutivas (intervalo mínimo possível)
	        List<Movie> winners = Arrays.asList(
	            createMovie(2008, "Movie A", "Producer X", true),
	            createMovie(2009, "Movie B", "Producer X", true),
	            createMovie(2012, "Movie C", "Producer Y", true),
	            createMovie(2015, "Movie D", "Producer Y", true)
	        );
	        when(movieRepository.findAllWinners()).thenReturn(winners);

	        AwardInterval result = producerService.getProducerIntervals();

	        assertNotNull(result);
	        assertNotNull(result.getMin());
	        assertEquals(1, result.getMin().size());
	        
	        ProducerInterval minInterval = result.getMin().get(0);
	        assertEquals("Producer X", minInterval.getProducer());
	        assertEquals(1, minInterval.getInterval());
	        assertEquals(2008, minInterval.getPreviousWin());
	        assertEquals(2009, minInterval.getFollowingWin());
	    }

	    @Test
	    @DisplayName("Teste 2: Deve calcular intervalo máximo corretamente")
	    void shouldCalculateMaximumIntervalCorrectly() {
	        // Given - Produtor com grande intervalo entre vitórias
	        List<Movie> winners = Arrays.asList(
	            createMovie(1990, "Movie A", "Producer X", true),
	            createMovie(2015, "Movie B", "Producer X", true), // 25 anos
	            createMovie(2008, "Movie C", "Producer Y", true),
	            createMovie(2009, "Movie D", "Producer Y", true)  // 1 ano
	        );
	        when(movieRepository.findAllWinners()).thenReturn(winners);

	        AwardInterval result = producerService.getProducerIntervals();

	        assertNotNull(result.getMax());
	        assertEquals(1, result.getMax().size());
	        
	        ProducerInterval maxInterval = result.getMax().get(0);
	        assertEquals("Producer X", maxInterval.getProducer());
	        assertEquals(25, maxInterval.getInterval());
	        assertEquals(1990, maxInterval.getPreviousWin());
	        assertEquals(2015, maxInterval.getFollowingWin());
	    }

	    @Test
	    @DisplayName("Teste 3: Deve tratar produtor com múltiplos intervalos")
	    void shouldHandleProducerWithMultipleIntervals() {
	        // Produtor com 3 vitórias (2 intervalos)
	        List<Movie> winners = Arrays.asList(
	            createMovie(1990, "Movie A", "Producer X", true),
	            createMovie(1992, "Movie B", "Producer X", true), // Intervalo: 2 anos
	            createMovie(2010, "Movie C", "Producer X", true)  // Intervalo: 18 anos
	        );
	        when(movieRepository.findAllWinners()).thenReturn(winners);

	        AwardInterval result = producerService.getProducerIntervals();

	        assertNotNull(result);
	        assertNotNull(result.getMin());
	        assertNotNull(result.getMax());
	        
	        // Min deve ser 2 anos (1990->1992)
	        ProducerInterval minInterval = result.getMin().get(0);
	        assertEquals("Producer X", minInterval.getProducer());
	        assertEquals(2, minInterval.getInterval());
	        
	        // Max deve ser 18 anos (1992->2010)
	        ProducerInterval maxInterval = result.getMax().get(0);
	        assertEquals("Producer X", maxInterval.getProducer());
	        assertEquals(18, maxInterval.getInterval());
	    }

	    @Test
	    @DisplayName("Teste 4: Trata produtores com vírgula E 'and'")
	    void shouldHandleProducersWithCommaAndAnd() {
	        // Produtores com formato complexo
	        List<Movie> winners = Arrays.asList(
	            createMovie(2008, "Movie A", "Producer A, Producer B and Producer C", true),
	            createMovie(2009, "Movie B", "Producer A", true),
	            createMovie(2011, "Movie C", "Producer B", true),
	            createMovie(2020, "Movie D", "Producer C", true)
	        );
	        when(movieRepository.findAllWinners()).thenReturn(winners);

	        AwardInterval result = producerService.getProducerIntervals();

	        assertNotNull(result);
	        assertNotNull(result.getMin());
	        assertNotNull(result.getMax());
	        
	        ProducerInterval minInterval = result.getMin().get(0);
	        assertEquals("Producer A", minInterval.getProducer());
	        assertEquals(1, minInterval.getInterval());
	        
	        ProducerInterval maxInterval = result.getMax().get(0);
	        assertEquals("Producer C", maxInterval.getProducer());
	        assertEquals(12, maxInterval.getInterval());
	    }

	    private Movie createMovie(Integer year, String title, String producers, Boolean winner) {
	        Movie movie = new Movie();
	        movie.setYear(year);
	        movie.setTitle(title);
	        movie.setStudios("Test Studio");
	        movie.setProducers(producers);
	        movie.setWinner(winner);
	        return movie;
	    }

}
