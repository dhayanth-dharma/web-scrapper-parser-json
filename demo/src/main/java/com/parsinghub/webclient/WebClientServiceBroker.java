package com.parsinghub.webclient;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

//import reactor.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class WebClientServiceBroker {
	private final WebClient client;
	private WebClientServiceBroker()
	{
		client = WebClient
				  .builder()
				    .baseUrl("https://www.parsehub.com/api/v2/")
				    .defaultCookie("cookieKey", "cookieValue")
				    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
//				    .defaultUriVariables(Collections.singletonMap("url", "https://jsonplaceholder.typicode.com"))
				    .defaultUriVariables(Collections.singletonMap("url", "https://www.parsehub.com/api/v2/"))
				  .build();
	}
	
	public WebClient.ResponseSpec getTest()
	{
		WebClient.RequestBodySpec uri = client
				  .method(HttpMethod.GET)
				  .uri("/todos/1");
		
		 WebClient.ResponseSpec response = uri
				    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();
		 return response;
	}
	public Mono<ResponseEntity<String>> post(String url, MultiValueMap<String, String> body)
	{
		  BodyInserter<MultiValueMap<String, String>, ClientHttpRequest>
		  inserter = BodyInserters.fromFormData(body);
		
		  WebClient.RequestBodySpec uri = client
				  .method(HttpMethod.POST)
				  .uri(url);
		  Mono<ResponseEntity<String>> result  = uri
				  .body(inserter)
				    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				    .exchange()
		 			.flatMap(response -> response.toEntity(String.class));
		  return result;
		
	}

	public Mono<ResponseEntity<String>> getTest2()
	{
		WebClient.RequestBodySpec uri = client
				  .method(HttpMethod.GET)
				  .uri("/todos/1");
		
	
		Mono<ResponseEntity<String>> result = uri
				    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				    .exchange()
		 			.flatMap(response -> response.toEntity(String.class));
//				Mono<ResponseEntity<String>> result = client.post().uri("/resource")
//			    .exchange()
//			    .flatMap(response -> response.toEntity(String.class))
//			    .flatMap(entity -> {
//			        // return Mono.just(entity) or Mono.error() depending on the response 
//			    });
		return result;
	}
	public void postTest()
	{
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		  body.set("name 1", "value 1");
		  body.add("name 2", "value 2+1");
		  body.add("name 2", "value 2+2");
		  body.add("name 3", null);
		  BodyInserter<MultiValueMap<String, String>, ClientHttpRequest>
		  inserter = BodyInserters.fromFormData(body);
		
		  WebClient.RequestBodySpec uri = client
				  .method(HttpMethod.POST)
				  .uri("/todos/1");
		  WebClient.ResponseSpec response1 = uri
				  .body(inserter)
				    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();
	}
	
}
