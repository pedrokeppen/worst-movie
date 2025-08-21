package com.br.outsera.worstmovie.file;

import com.br.outsera.worstmovie.entity.Movie;
import com.br.outsera.worstmovie.repository.MovieRepository;
import com.br.outsera.worstmovie.util.ReportLogsUtil;

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
			loadLinesFromCsv();
		}
	}

	private void loadLinesFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("csv/movielist.csv");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            List<Movie> movies = new ArrayList<>();
            String line;
            ReportLogsUtil reportLogsUtil = new ReportLogsUtil();
            
            while ((line = reader.readLine()) != null) {
            	reportLogsUtil.setLineCount(reportLogsUtil.getLineCount() + 1);
                
                // Skip header
                if (reportLogsUtil.getLineCount() == 1) {
                    log.debug("Header: {}", line);
                    continue;
                }
                
                try {
                    Movie movie = parseLine(line, reportLogsUtil.getLineCount());
                    if (movie != null) {
                        movies.add(movie);
                        reportLogsUtil.setSuccessParsedCount(reportLogsUtil.getSuccessParsedCount() + 1);
                    }
                } catch (Exception e) {
                	reportLogsUtil.setErrorsCount(reportLogsUtil.getErrorsCount() + 1);
                    log.warn("Error parsing line {}: '{}' - {}", reportLogsUtil.getLineCount(), line, e.getMessage());
                }
            }
            
            reader.close();
            
            if (!movies.isEmpty()) {
                movieRepository.saveAll(movies);
                logReports(movies, reportLogsUtil);
            }
            
        } catch (Exception e) {
            log.error("Error loading movies from CSV", e);
        }
    }

	private void logReports(List<Movie> movies, ReportLogsUtil reportLogsUtil) {
		log.info("CSV Processing completed:");
		log.info("Total lines read: {}", reportLogsUtil.getLineCount());
		log.info("Successfully parsed: {}", reportLogsUtil.getSuccessParsedCount());
		log.info("Errors: {}", reportLogsUtil.getErrorsCount());
		log.info("Movies saved to database: {}", movies.size());
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
            String yearStr = verifyIsEmpty (line, lineNumber, data, movie, 0, "year");
            movie.setYear(Integer.parseInt(yearStr));
            
            // Parse title
            String title = verifyIsEmpty(line, lineNumber, data, movie, 1, "title");
            movie.setTitle(title);
            
            // Parse studios (can be empty)
            movie.setStudios(data[2].trim());
            
            // Parse producers
            String producers = verifyIsEmpty(line, lineNumber, data, movie, 3, "producers");
            movie.setProducers(producers);
            
            // Parse winner
            String winnerStr = data[4].trim();
            movie.setWinner("yes".equalsIgnoreCase(winnerStr));
            
            log.debug("Parsed movie: {} ({}) - Winner: {}", title, movie.getYear(), movie.getWinner());
            return movie;
            
        } catch (Exception e) {
            log.warn("Line {} parsing error: '{}' - {}", lineNumber, line, e.getMessage());
            return null;
        }
    }

	private String verifyIsEmpty(String line, int lineNumber, String[] data, Movie movie, int position, String type) {
		String text = data[position].trim();
		if (text.isEmpty()) {
		    log.warn("Line {} has empty {}: '{}'", lineNumber, type, line);
		    return null;
		}
		
		return text;
	}
}