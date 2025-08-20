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
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

			List<Movie> movies = new ArrayList<>();
			String line;
			boolean isFirstLine = true;

			while ((line = reader.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false;
					continue; // Skip header
				}

				String[] data = line.split(";");
				if (data.length >= 5) {
					Movie movie = new Movie();
					movie.setYear(Integer.parseInt(data[0].trim()));
					movie.setTitle(data[1].trim());
					movie.setStudios(data[2].trim());
					movie.setProducers(data[3].trim());
					movie.setWinner("yes".equalsIgnoreCase(data[4].trim()));

					movies.add(movie);
				}
			}

			movieRepository.saveAll(movies);
			log.info("Loaded {} movies from CSV", movies.size());

			reader.close();
		} catch (Exception e) {
			log.error("Error loading movies from CSV", e);
		}
	}
}