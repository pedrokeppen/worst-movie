package com.br.outsera.worstmovie.file;

import com.br.outsera.worstmovie.entity.Movie;
import com.br.outsera.worstmovie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CSVReader implements ApplicationRunner {

	private final MovieRepository movieRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (movieRepository.count() == 0) {
			loadMoviesFromCsv();
		}
	}

	private void loadMoviesFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("csv/movielist.csv");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            List<Movie> movies = new ArrayList<>();
            String line;
            int lineNumber = 0;
            int successfullyParsed = 0;
            int errors = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header
                if (lineNumber == 1) {
                    log.debug("Header: {}", line);
                    continue;
                }
                
                try {
                    Movie movie = parseLine(line, lineNumber);
                    if (movie != null) {
                        movies.add(movie);
                        successfullyParsed++;
                    }
                } catch (Exception e) {
                    errors++;
                    log.warn("Error parsing line {}: '{}' - {}", lineNumber, line, e.getMessage());
                }
            }
            
            reader.close();
            
            if (!movies.isEmpty()) {
                movieRepository.saveAll(movies);
                log.info("CSV Processing completed:");
                log.info("- Total lines read: {}", lineNumber);
                log.info("- Successfully parsed: {}", successfullyParsed);
                log.info("- Errors: {}", errors);
                log.info("- Movies saved to database: {}", movies.size());
            } else {
                log.error("No movies were parsed from CSV!");
            }
            
        } catch (Exception e) {
            log.error("Error loading movies from CSV", e);
        }
    }
    
    private Movie parseLine(String line, int lineNumber) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        // Split by semicolon, but handle potential issues
        String[] data = line.split(";", -1); // -1 keeps empty strings
        
        if (data.length < 5) {
            log.warn("Line {} has insufficient columns ({}): '{}'", lineNumber, data.length, line);
            return null;
        }
        
        try {
            Movie movie = new Movie();
            
            // Parse year
            String yearStr = data[0].trim();
            if (yearStr.isEmpty()) {
                log.warn("Line {} has empty year: '{}'", lineNumber, line);
                return null;
            }
            movie.setYear(Integer.parseInt(yearStr));
            
            // Parse title
            String title = data[1].trim();
            if (title.isEmpty()) {
                log.warn("Line {} has empty title: '{}'", lineNumber, line);
                return null;
            }
            movie.setTitle(title);
            
            // Parse studios (can be empty)
            movie.setStudios(data[2].trim());
            
            // Parse producers
            String producers = data[3].trim();
            if (producers.isEmpty()) {
                log.warn("Line {} has empty producers: '{}'", lineNumber, line);
                return null;
            }
            movie.setProducers(producers);
            
            // Parse winner
            String winnerStr = data[4].trim();
            movie.setWinner("yes".equalsIgnoreCase(winnerStr));
            
            log.debug("Parsed movie: {} ({}) - Winner: {}", title, movie.getYear(), movie.getWinner());
            return movie;
            
        } catch (NumberFormatException e) {
            log.warn("Line {} has invalid year format: '{}'", lineNumber, line);
            return null;
        } catch (Exception e) {
            log.warn("Line {} parsing error: '{}' - {}", lineNumber, line, e.getMessage());
            return null;
        }
    }
}