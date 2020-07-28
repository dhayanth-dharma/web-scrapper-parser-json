package com.example.demo.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ParsingService;




@RestController
@RequestMapping(path = "/api/")
public class ParsingController {

	
	@Autowired
	private ParsingService parsingService;
	
	@GetMapping(path = "parse/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String setup(@PathVariable("fileName") String fileName){
		
		try {
			parsingService.parse(fileName);
			return "ok";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	
	
	@GetMapping(path = "range/{range}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> range(@PathVariable("range") int range){
		try {
			int limit=range+5000;
			List<String> codes=new ArrayList<String>();
			for(; range<limit; range++)
			{
				codes.add(String.valueOf(range));
			}
			return codes;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
}
