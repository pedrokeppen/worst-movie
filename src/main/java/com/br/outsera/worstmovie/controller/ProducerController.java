package com.br.outsera.worstmovie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.br.outsera.worstmovie.service.ProducerService;

import io.swagger.api.ProducerApi;
import io.swagger.model.AwardInterval;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ProducerController  implements ProducerApi {

	private final ProducerService producerService;

    @Override
    public ResponseEntity<AwardInterval> getIntervals() {
        AwardInterval intervals = producerService.getProducerIntervals();
        return ResponseEntity.ok(intervals);
    }

}
