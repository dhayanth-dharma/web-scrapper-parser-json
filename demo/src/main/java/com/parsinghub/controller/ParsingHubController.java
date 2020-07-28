package com.parsinghub.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.service.ParsingService;
import com.parsinghub.service.ParsingHubService;
import com.parsinghub.webclient.WebClientServiceBroker;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping(path = "/api/ph/")
public class ParsingHubController {
	@Autowired
	private ParsingHubService parsingService;
	
	
	@GetMapping(path = "auth", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<String>> setup(){
		
		try {
			Mono<ResponseEntity<String>> res= parsingService.run();
			return res;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@GetMapping(path = "get", produces = MediaType.APPLICATION_JSON_VALUE)
	public String get(){
		
		try {
			 parsingService.getTemplate();
			return "ok";
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GetMapping(path = "get/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getTest(@PathVariable("fileName") String fileName){
		
		try {
//			parsingService.parse(fileName);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	
}
