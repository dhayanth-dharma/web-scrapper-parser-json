package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;


//XML
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ParsingService {

	@Value("${data.src}")
	private String dataDir;	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	
	public void parse(String fileName) throws JsonParseException, JsonMappingException, IOException
	{
		//xml parsing
		  XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Scrapper Results ");
	        createHeaderRow(sheet);
		
	        
	  //json parsing
		
		final File initialFile = new File(dataDir+fileName+".json");
        final InputStream inputs = new FileInputStream(initialFile);
	      
		ObjectMapper mapper = new ObjectMapper();
	    JsonNode jsonNode = mapper.readValue(inputs,
	                        JsonNode.class);
	    String jsonString = mapper.writeValueAsString(jsonNode);
//	    System.out.println(jsonString);
	    
	    int count=1;
    	//
    	JSONObject jsonObj = new JSONObject(jsonString);
    	JSONArray result = jsonObj.getJSONArray("results");
	    for (int i = 0 ; i < result.length(); i++) {
	    	
	    	
	    	
	        JSONObject obj = result.getJSONObject(i);
	        String keyWord = "";
	        if(obj.has("keyword")){
	        	keyWord=obj.getString("keyword");
	        }
    		
	        JSONArray standards=null;
	        try {
	        	 standards = obj.getJSONArray("standards");
			} catch (Exception e) {
				continue;
			}
	        
	        for (int k = 0 ; k < standards.length(); k++) {
	        	Row row = sheet.createRow(count);
	        	JSONObject standard = standards.getJSONObject(k);
	        	String name="";
        		String ics="";
        	    String url="";
        	    String code="";
        	    String title="";
        	    String absctract="";
	        	if(standard.has("name")){
	        		 name=standard.getString("name");
	        	}
	        	if(standard.has("ics")){
	        		 ics=standard.getString("ics");
	        	}
	        	if(standard.has("url")){
	        		 url=standard.getString("url");
	        	}
	        	if(standard.has("code")){
	        		 code=standard.getString("code");
	        	}
	        	if(standard.has("title")){
	        		 title=standard.getString("title");
	        	}
	        	if(standard.has("abstract")){
	        		absctract=standard.getString("abstract");
	        	}
        		//String group_info=standard.getString("group_info");

	        	String published_status="";
        		String pub_date="";
        		String edition="";
        		String pages="";
        		String tech_commitee="";
        		String info_ics="";;
	        	
        		String stage= "";
        		String revisions="";
	        	//parsing info
        		if(standard.has("info")){
	        	JSONArray infos = standard.getJSONArray("info");
		        	for (int l = 0 ; l < infos.length(); l++) {
		        		JSONObject info = infos.getJSONObject(l);
		        		if(info.has("status")){
		        		 published_status=info.getString("status");
		        		}
		        		if(info.has("pub_date")){
		        			pub_date=info.getString("pub_date");
			        		}
		        		if(info.has("edition")){
		        			 edition=info.getString("edition");
			        		}
		        		if(info.has("pages")){
		        			 pages=info.getString("pages");
			        		}
		        		if(info.has("tech_commitee")){
		        			tech_commitee=info.getString("tech_commitee");
			        		}
		        		if(info.has("ics")){
		        			info_ics=info.getString("ics");
			        		}
		        		}
        		}
	        	//parsing life cycle
        		if(standard.has("life_cycle")){
		        	JSONArray life_cycles = standard.getJSONArray("life_cycle");
		        	for (int m = 0 ; m < life_cycles.length(); m++) {
		        		JSONObject life_cycle = life_cycles.getJSONObject(m);
		        		if(life_cycle.has("stage")){
		        			stage=life_cycle.getString("stage").replace("\n", " ");
			        		}
		        		if(life_cycle.has("revisions")){
		        			revisions=life_cycle.getString("revisions").replace("\n", " ");
			        		}
		        	}
        		}
        		if(title!=null || title !="")
        		{
        			Cell cell1 = row.createCell(1);
    		    	cell1.setCellValue((String) keyWord); 
    		    	Cell cell2 = row.createCell(2);
    		    	cell2.setCellValue((String) name);
    		    	Cell cell3 = row.createCell(3);
    		    	cell3.setCellValue((String) ics);
    		    	Cell cell4 = row.createCell(4);
    		    	cell4.setCellValue((String) url);
    		    	Cell cell5 = row.createCell(5);
    		    	cell5.setCellValue((String) code);
    		    	Cell cell6 = row.createCell(6);
    		    	cell6.setCellValue((String) title);
    		    	
    		    	Cell cell7 = row.createCell(7);
    		    	cell7.setCellValue((String) published_status); 
    		    	Cell cell8 = row.createCell(8);
    		    	cell8.setCellValue((String) pub_date);
    		    	Cell cell9 = row.createCell(9);
    		    	cell9.setCellValue((String) edition);
    		    	Cell cell10 = row.createCell(10);
    		    	cell10.setCellValue((String) pages);
    		    	Cell cell11 = row.createCell(11);
    		    	cell11.setCellValue((String) tech_commitee);
    		    	Cell cell12 = row.createCell(12);
    		    	cell12.setCellValue((String) info_ics);
    	        	
    		    	Cell cell13 = row.createCell(13);
    		    	cell13.setCellValue((String) stage);
    		    	Cell cell14 = row.createCell(14);
    		    	cell14.setCellValue((String) revisions);
    		    	Cell cell15 = row.createCell(15);
    		    	cell15.setCellValue((String) absctract);
    		    	count++;
    		    	//cellStyle	    	
//    		    	cell5.setCellStyle(getFontStyle(false, sheet));
        		}
	        	
	        }
	    }
	    
	    try (FileOutputStream outputStream = new FileOutputStream("src/main/resources/data/"+fileName+".xlsx")) {
	          workbook.write(outputStream);
	        }
	}
	
	

	
	
	private CellStyle getFontStyle(boolean correct, Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		if(correct){
		    Font font = sheet.getWorkbook().createFont();
		    font.setBoldweight((short)2);
		    font.setFontHeightInPoints((short) 16);
		    font.setColor(Font.COLOR_NORMAL);
		    cellStyle.setFont(font);
		}
		else{
		    Font font = sheet.getWorkbook().createFont();
		    font.setBoldweight((short)2);
		    font.setFontHeightInPoints((short) 16);
		    font.setColor(Font.COLOR_RED);
		    cellStyle.setFont(font);
		}
		return cellStyle;
	}
	
	private void createHeaderRow(Sheet sheet) {
		 
	    CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
	    Font font = sheet.getWorkbook().createFont();
	    font.setBoldweight((short)2);
	    font.setFontHeightInPoints((short) 16);
	    font.setColor(Font.COLOR_RED);
	    cellStyle.setFont(font);
	 
	    Row row = sheet.createRow(0);
	    Cell cell1 = row.createCell(1);
    	cell1.setCellValue("KEYWORD"); 
    	Cell cell2 = row.createCell(2);
    	cell2.setCellValue("NAME");
    	Cell cell3 = row.createCell(3);
    	cell3.setCellValue("ICS_CODE");
    	Cell cell4 = row.createCell(4);
    	cell4.setCellValue("URL");
    	Cell cell5 = row.createCell(5);
    	cell5.setCellValue("ISO_CODE");
    	Cell cell6 = row.createCell(6);
    	cell6.setCellValue("TITLE");
    	
    	Cell cell7 = row.createCell(7);
    	cell7.setCellValue("P_STATUS"); 
    	Cell cell8 = row.createCell(8);
    	cell8.setCellValue("P_DATE");
    	Cell cell9 = row.createCell(9);
    	cell9.setCellValue("EDITION");
    	Cell cell10 = row.createCell(10);
    	cell10.setCellValue("PAGES");
    	Cell cell11 = row.createCell(11);
    	cell11.setCellValue("TECH_COMMITTE");
    	Cell cell12 = row.createCell(12);
    	cell12.setCellValue("INFO_ICS");
    	
    	Cell cell13 = row.createCell(13);
    	cell13.setCellValue("R_STAGE");
    	Cell cell14 = row.createCell(14);
    	cell14.setCellValue("REVISION");
    	Cell cell15 = row.createCell(15);
    	cell15.setCellValue("ABSCTRACT");
	}
	
	
	
}
