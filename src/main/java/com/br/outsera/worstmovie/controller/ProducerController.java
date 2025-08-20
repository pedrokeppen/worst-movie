package com.br.outsera.worstmovie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.api.ProducerApi;
import io.swagger.model.AwardInterval;

@RestController
public class ProducerController  implements ProducerApi {

	@Override
	public ResponseEntity<AwardInterval> getIntervals() {
		// TODO Implementar chamada ao service 
		
		return null;
	}

}
