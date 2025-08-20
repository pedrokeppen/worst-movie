package com.br.outsera.worstmovie.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.br.outsera.worstmovie.entity.Movie;
import com.br.outsera.worstmovie.repository.MovieRepository;
import com.br.outsera.worstmovie.service.ProducerService;

import io.swagger.model.AwardInterval;
import io.swagger.model.ProducerInterval;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProducerServiceImpl implements ProducerService {
	
	private final MovieRepository movieRepository;

	@Override
	public AwardInterval getProducerIntervals() {
		 List<Movie> winners = movieRepository.findAllWinners();
	        
	        // Mapear produtores e seus anos de vitória
	        Map<String, List<Integer>> producerWins = new HashMap<>();
	        
	        for (Movie movie : winners) {
	            String[] producers = parseProducers(movie.getProducers());
	            for (String producer : producers) {
	                producerWins.computeIfAbsent(producer, k -> new ArrayList<>()).add(movie.getYear());
	            }
	        }
	        
	        // Calcular intervalos para produtores com múltiplas vitórias
	        List<ProducerInterval> intervals = new ArrayList<>();
	        
	        for (Map.Entry<String, List<Integer>> entry : producerWins.entrySet()) {
	            String producer = entry.getKey();
	            List<Integer> years = entry.getValue();
	            
	            if (years.size() > 1) {
	                Collections.sort(years);
	                for (int i = 1; i < years.size(); i++) {
	                    int interval = years.get(i) - years.get(i - 1);
	                    ProducerInterval producerInterval = buildProducerInterval(producer, years, i, interval);
	                    intervals.add(producerInterval);
	                }
	            }
	        }
	        
	        if (intervals.isEmpty()) {
	            return new AwardInterval();
	        }
	        
	        // Encontrar intervalos mínimos 
	        int minInterval = intervals.stream()
	        		.mapToInt(ProducerInterval::getInterval)
	        		.min()
	        		.orElse(0);
	        
	        // Encontrar intervalos máximos 
	        int maxInterval = intervals
	        		.stream()
	        		.mapToInt(ProducerInterval::getInterval)
	        		.max()
	        		.orElse(0);
	        
	        List<ProducerInterval> minIntervals = intervals.stream()
	                .filter(pi -> pi.getInterval() == minInterval)
	                .collect(Collectors.toList());
	        
	        List<ProducerInterval> maxIntervals = intervals.stream()
	                .filter(pi -> pi.getInterval() == maxInterval)
	                .collect(Collectors.toList());
	        
	        AwardInterval result = new AwardInterval();
	        result.setMin(minIntervals);
	        result.setMax(maxIntervals);
	        
	        return result;
	}

	private ProducerInterval buildProducerInterval(String producer, List<Integer> years, int i, int interval) {
		ProducerInterval producerInterval = new ProducerInterval();
		producerInterval.setProducer(producer);
		producerInterval.setInterval(interval);
		producerInterval.setPreviousWin(years.get(i - 1));
		producerInterval.setFollowingWin(years.get(i));
		return producerInterval;
	}
	
	private String[] parseProducers(String producers) {
	    if (producers == null || producers.trim().isEmpty()) {
	        return new String[0];
	    }
	    
	    return Arrays.stream(producers.split(","))           // Divide por vírgula
	            .map(String::trim)                           // Remove espaços
	            .filter(part -> !part.isEmpty())             // Remove vazios
	            .flatMap(this::splitByAnd)                   // Divide por "and"
	            .map(String::trim)                           // Remove espaços novamente
	            .filter(producer -> !producer.isEmpty())     // Remove vazios finais
	            .toArray(String[]::new);                     // Converte para array
	}

	private Stream<String> splitByAnd(String part) {
	    return part.contains(" and ") 
	        ? Arrays.stream(part.split(" and "))
	        : Stream.of(part);
	}

}
