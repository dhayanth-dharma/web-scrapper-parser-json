package com.parsinghub.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.parsinghub.webclient.WebClientServiceBroker;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
@Service
public class ParsingHubService {
	@Value("${data.src}")
	private String dataDir;	
	@Autowired
	private WebClientServiceBroker webClientServiceBroker;
	
	private static final Logger logger = LoggerFactory.getLogger(ParsingHubService.class);

//	http://xpather.com/
	public void getTemplate(){
		//XML
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Scrapper Results ");
		createHeaderRow(sheet);
		List<String> range=getRange(10001);
//		List<String> range=new ArrayList<String>();
//		range.add("5557");
//		range.add("8323");
//		range.add("8322");
////
////		
//		range.add("1");
//		range.add("70040");
		//4780 --page without abstract, check where exist in excel
	//		  70281 70040
		int count=1;

		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setAppletEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try { 	
		 for(String keyword: range)	{
			 try {
				 System.out.println("KEYWORD=============>>>>>"+ keyword);
				 System.out.println("KEYWORD=============>>>>>"+ keyword);
				 System.out.println("KEYWORD=============>>>>>"+ keyword);
				 System.out.println("KEYWORD=============>>>>>"+ keyword);
				 String searchUrl = "https://www.iso.org/standard/"+keyword+".html";
				  HtmlPage page = client.getPage(searchUrl);
				  Row row = sheet.createRow(count);
				  getElemenent(page, row, keyword, searchUrl, workbook);
				  count++;
			} catch (Exception e) {
				if( e instanceof com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException)
				{}
				else
				{count++;}
				logger.error("ERROR=============>>>>>", e);
				continue;
				
			}
			
		 }
		 try (FileOutputStream outputStream = new FileOutputStream("src/main/resources/data/exrtaction.xlsx")) {
	          workbook.write(outputStream);
	     }
		 client.close();
		}catch(Exception e){
		  e.printStackTrace();
		  logger.error("ERROR=============>>>>>", e);
		}	
	}
	
	/*
	 * RETURNS RANGE OF STRING NUMBERS
	 * */
	public List<String>  getRange(int range)
	{
		int limit=range+1000;
		List<String> codes=new ArrayList<String>();
		for(; range<limit; range++)
		{
			codes.add(String.valueOf(range));
		}
		return codes;
	}
	
	public void getElemenent(HtmlPage page, Row row, String keyWord, String url, XSSFWorkbook workbook ) throws IOException
	{
//		DomText error= page.getFirstByXPath(".//div[@class='error-code'][1]/text()");
//		if(error!=null || error.asText()!="")
//		return;
		logger.info("KEYWORD=============>>>>>", keyWord);
		//HEADING SECTION
		HtmlHeading1 isoCode=page.getFirstByXPath("//h1");
		HtmlHeading2 title=(HtmlHeading2) page.getFirstByXPath("//h2[@class='no-uppercase']");
		HtmlElement abstractDiv=null;
		List<HtmlElement> abstractPtags=null;
		boolean isRevised=false;
		HtmlAnchor revisedByElem=null;
		try {
			if( null!= page.getFirstByXPath("//div[@itemprop='description']")){
				abstractDiv=page.getFirstByXPath("//div[@itemprop='description']");
			}
			if( null!= abstractDiv.getByXPath(".//p")){
				abstractPtags= ((List<HtmlElement>) abstractDiv.getByXPath(".//p"));
			}
		}
		catch(NullPointerException e){
			isRevised=true;
			revisedByElem=page.getFirstByXPath(".//section[@id='revised-by']//h3[1]/a");
		}
		
		
		
		
		
		
		
		//REVIEW STAGE SECTION
		HtmlElement reviewUl=null;
		try {
		 reviewUl= page.getFirstByXPath("//ul[@class='nav navbar-nav stages']");
		}catch (Exception e) {
		}
		HtmlListItem reviewListItem=null;
		try {
			reviewListItem= reviewUl.getFirstByXPath("//li[contains(@class,'dropdown') and contains(@class, 'active')]");
			}catch (Exception e) {
		}
		HtmlElement reviewStageCode=null;
		try {
			 reviewStageCode=reviewListItem.getFirstByXPath(".//span[@class='stage-code']");
			}catch (Exception e) {
		}
		HtmlElement reviewStageStatus=null;
		try {
			reviewStageStatus=reviewListItem.getFirstByXPath(".//div[@class='stage-title']");
			}catch (Exception e) {
		}
		reviewListItem.click();

		//REVIEW ANCHOR LIST SECTION (LIFE CYCLE)
		List<HtmlElement> reivewLiItems= (List<HtmlElement>) reviewListItem.getByXPath(".//ul[@class='dropdown-menu']/li/a");
		HtmlElement stageCodeSpan=null;
		HtmlElement stageTitleDiv=null;
		HtmlElement stageDateSpan=null;
		
		String stageCodeListString="[";
		String stageTitleListString="[";
		String stageDateListString="[";
		int j=0;
		for(HtmlElement htmlItem : reivewLiItems){
			try {
				stageCodeSpan=htmlItem .getFirstByXPath(".//span[@class='stage-code']");
				if(stageCodeSpan!=null)
					stageCodeListString+="("+j+")"+stageCodeSpan.asText()+",";
			}catch (Exception e) {
				logErrror(e);}
			try {
				stageDateSpan=htmlItem .getFirstByXPath(".//span[@class='stage-date']");
				if(stageDateSpan!=null)
					stageTitleListString+="("+j+")"+stageDateSpan.asText()+",";
			}catch (Exception e) {logErrror(e);
			}
			try {
				stageTitleDiv=htmlItem .getFirstByXPath(".//div[@class='stage-title']"); 
				if(stageTitleDiv!=null)
					stageDateListString+="("+j+")"+stageTitleDiv.asText()+",";
			}catch (Exception e) {logErrror(e);
			}
		}
		stageCodeListString+="]";
		stageTitleListString+="]";
		stageDateListString+="]";
		
		//GENERAL INFORMATION SECTION
		HtmlElement pubStatusElem=page.getFirstByXPath(".//ul[@class='refine']//li[1]/div[@class='row'][1]/div[@class='col-sm-6'][1]");
		HtmlElement pubDateElem=page.getFirstByXPath(".//ul[@class='refine']//li[1]/div[@class='row'][1]/div[@class='col-sm-6'][2]");
		HtmlElement editionElem=page.getFirstByXPath(".//ul[@class='refine']//li[2]/div[@class='row'][1]/div[@class='col-sm-6'][1]");
		HtmlElement noOfPageElem=page.getFirstByXPath(".//ul[@class='refine']//li[2]/div[@class='row'][1]/div[@class='col-sm-6'][2]");
		HtmlElement techCommiteeCodeElem=page.getFirstByXPath(".//li[3]/div[@class='clearfix'][1]/div[2]");
		HtmlAnchor techCommiteeCodeHrefElem=page.getFirstByXPath(".//li[3]/div[@class='clearfix'][1]/div[2]/a");
		HtmlElement techCommiteeDetailElem=page.getFirstByXPath(".//li[3]/div[@class='clearfix'][1]/div[3]");
		
		List<HtmlAnchor> icsAnchorElemList= (List<HtmlAnchor>) page.getByXPath(".//li[4]//div[@class='entry-name entry-block']/a");//ics code
		List<HtmlAnchor> icsAnchorHrefElemList= (List<HtmlAnchor>) page.getByXPath(".//li[4]//div[@class='entry-name entry-block']/a");//icsLink
		List<DomText > icsDetaiilsElemList=  (List<DomText >) page.getByXPath(".//li[4]//div[@class='entry-title']/text()");
//		List<HtmlElement> icsDetaiilsElemList=  (List<HtmlElement>) page.getByXPath(".//li[4]//div[@class='entry-title']/text()");
		String icsCodeText="[";
		String icsLinkText="[";
		String icsDetails="[";
		String icsDetailComplete="";
		for(int k=0; k<icsAnchorElemList.size(); k++){
			if(icsAnchorElemList!=null)
			icsCodeText+="("+k+")"+icsAnchorElemList.get(k).asText()+",";
			if(icsAnchorHrefElemList!=null)
			icsLinkText+="("+k+")"+"https://www.iso.org"+icsAnchorHrefElemList.get(k).getHrefAttribute()+",";
			if(icsDetaiilsElemList!=null)
			icsDetails+="("+k+")"+icsDetaiilsElemList.get(k).asText()+",";
			if(icsCodeText!=null && icsCodeText!=null &&icsDetails!=null )
			icsDetailComplete+="["+icsCodeText+icsLinkText+icsDetails+"]";
		}
		icsCodeText+="]";
		icsLinkText+="]";
		icsDetails+="]";
		
		//PRICE
		HtmlElement priceSpan=null;
		try {
			priceSpan=page.getFirstByXPath(".//span[@id='productPrice' and @class ='amount']");
		} catch (Exception e) {logErrror(e);
		}
		
		HtmlElement currencyELem=null;
		try {
			currencyELem=page.getFirstByXPath(".//span[ @class ='currency']");
		} catch (Exception e) {
			logErrror(e);
		}
				
		
		
		//PREVIOUS
		HtmlElement previousElem=null;
		try {
			previousElem=page.getFirstByXPath(".//ul[@class='steps']/li[1]");
		} catch (Exception e) {logErrror(e);
		}
		HtmlElement nowConfirmedElem=null;
		try {
			nowConfirmedElem=page.getFirstByXPath(".//ul[ @class ='steps']/li[2]");
//			nowConfirmedElem.getByXPath("./a"); //TO GET LINK OF NEW ISO
		} catch (Exception e) {logErrror(e);
		}
		
		
		
		
		//CREATING XML
		//==============================================
    	//************HEADING***************
    	//==============================================
		if(keyWord!=null ) {
			try {
				Cell cell1 = row.createCell(1);
		    	cell1.setCellValue((String) keyWord); 
			}catch (Exception e) {logErrror(e);
			}
		
		}
		if(isoCode!=null ) {
			try {
				Cell cell2 = row.createCell(2);
		    	cell2.setCellValue((String) isoCode.asText()); 
			}catch (Exception e) {logErrror(e);
			}
			
		}
    	
    	if(title!=null ) {
    		try {
    			Cell cell3 = row.createCell(3);
    	    	cell3.setCellValue((String) title.asText());
			}catch (Exception e) {logErrror(e);
			}
			
		}
    	
    	if(pubStatusElem!=null ) {
    		try {
    			Cell cell4 = row.createCell(4);
            	cell4.setCellValue((String) pubStatusElem.asText()); 
			}catch (Exception e) {logErrror(e);
			}
    		 
		}
    	
    	if(pubDateElem!=null ) {
    		try {
    			Cell cell5 = row.createCell(5);
            	cell5.setCellValue((String) pubDateElem.asText());
			}catch (Exception e) {logErrror(e);
			}
    		
		}
    	
    	if(editionElem!=null ) {
    		try {
    			Cell cell6 = row.createCell(6);
            	cell6.setCellValue((String) editionElem.asText());
			}catch (Exception e) {logErrror(e);
			}
    		
		}
    	
    	if(noOfPageElem!=null ) {
    		try {
    			Cell cell7 = row.createCell(7);
            	cell7.setCellValue((String) noOfPageElem.asText());
			}catch (Exception e) {logErrror(e);
			}
    		
    		
		}
    	//==============================================
    	//************GENERAL INFORMATION***************
    	//==============================================
    	//TECH COMMITTE CODE
    	if(techCommiteeCodeElem!=null ) {
    		try {
    			org.apache.poi.ss.usermodel.Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
            	hyperlink.setAddress("https://www.iso.org/"+techCommiteeCodeHrefElem.getHrefAttribute());
            	Cell cell8 = row.createCell(8);
            	cell8.setCellValue((String) techCommiteeCodeElem.asText());
            	cell8.setHyperlink(hyperlink);
			}catch (Exception e) {logErrror(e);
				try {
					Cell cell8 = row.createCell(8);
	            	cell8.setCellValue((String) techCommiteeCodeElem.asText());
				} catch (Exception e2) {
					logErrror(e);
				}
			}
		}
    	
    	//TECH COMMITTE
    	if(techCommiteeDetailElem!=null ) {
    		try {
    			Cell cell9 = row.createCell(9);
            	cell9.setCellValue((String) techCommiteeDetailElem.asText());
			}catch (Exception e) {logErrror(e);
			}
    		
    
		}
    	
    	//ICS CODE
    	if(icsCodeText!=null ) {
    		try {
    			Cell cell10 = row.createCell(10);
            	cell10.setCellValue((String) icsCodeText);
			}catch (Exception e) {logErrror(e);
			}
    		
    	
		}
    	
    	//ICS CODE
    	if(icsLinkText!=null ) {
    		try {

        		Cell cell11 = row.createCell(11);
            	cell11.setCellValue((String) icsLinkText);
			}catch (Exception e) {logErrror(e);
			}
    		
		}
    	
    	//ICS CODE
    	if(icsDetails!=null ) {
    		try {

        		Cell cell12 = row.createCell(12);
            	cell12.setCellValue((String) icsDetails);
			}catch (Exception e) {logErrror(e);
			}
    		
		}
    	
    	//==============================================
    	//************LIFE CYCLE ***************
    	//==============================================
    	if(reviewStageCode!=null ) {
    		try {
    			Cell cell13 = row.createCell(13);
            	cell13.setCellValue((String) reviewStageCode.asText());
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(reviewStageStatus!=null ) {
    		try {
    			Cell cell14 = row.createCell(14);
            	cell14.setCellValue((String) reviewStageStatus.asText());	
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(stageCodeListString!=null ) {
    		try {
    			Cell cell15 = row.createCell(15);
            	cell15.setCellValue((String) stageCodeListString);; 
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(stageTitleListString!=null ) {
    		try {
    			Cell cell16 = row.createCell(16);
            	cell16.setCellValue((String) stageTitleListString);
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(stageDateListString!=null ) {
    		try {
    			Cell cell17 = row.createCell(17);
            	cell17.setCellValue((String) stageDateListString); 
			}catch (Exception e) {logErrror(e);
			}
		}
    	//==============================================
    	//************META INFO***************
    	//==============================================
    	if(revisedByElem!=null ) {
    		try {
    		  	org.apache.poi.ss.usermodel.Hyperlink hyperlinkRevisedBy = workbook.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
            	hyperlinkRevisedBy.setAddress("https://www.iso.org/"+revisedByElem.getHrefAttribute());
            	Cell cell18 = row.createCell(18);
            	cell18.setCellValue((String) revisedByElem.asText());
            	cell18.setHyperlink(hyperlinkRevisedBy);
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(priceSpan!=null ) {
    		try {
    			
            	Cell cell19 = row.createCell(19);
            	cell19.setCellValue((String) priceSpan.asText()+":"+currencyELem.asText());
			}catch (Exception e) {logErrror(e);
			}
		}    	
    	if(previousElem!=null ) {
    		try {
    			Cell cell20 = row.createCell(20);
            	cell20.setCellValue((String) previousElem.asText());
			}catch (Exception e) {logErrror(e);
			}
		}    
    	if(nowConfirmedElem!=null ) {
    		try {
            	Cell cell21 = row.createCell(21);
            	cell21.setCellValue((String) nowConfirmedElem.asText());
			}catch (Exception e) {logErrror(e);
			}
		}  
   
    	if(abstractPtags!=null ) {
    		try {
    			Cell cell22 = row.createCell(22);
            	cell22.setCellValue((String) abstractPtags.get(1).asText());
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(url!=null ) {
    		try {
    			Cell cell23 = row.createCell(23);
            	cell23.setCellValue((String) url);
			}catch (Exception e) {logErrror(e);
			}
		}
    	if(icsDetailComplete!=null ) {
    		try {
    			Cell cell24 = row.createCell(24);
            	cell24.setCellValue((String) icsDetailComplete);
			}catch (Exception e) {logErrror(e);
			}
		}
    	
	}
	
	public void getTemplate2()
	{
		String searchQuery = "Iphone 6s" ;
		
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
		  String searchUrl = "https://newyork.craigslist.org/search/sss?sort=rel&query=" + URLEncoder.encode(searchQuery, "UTF-8");
		  HtmlPage page = client.getPage(searchUrl);
		  getElemenent2(page);
		  client.close();
		}catch(Exception e){
		  e.printStackTrace();
		}
		
	}
	public void logErrror(Exception e)
	{
		logger.error("ERROR=============>>>>>", e);
	}
	
	public void getElemenent2(HtmlPage page)
	{
		List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//li[@class='result-row']") ;
		
		if(items.isEmpty()){
		  System.out.println("No items found !");
		}else{
		for(HtmlElement htmlItem : items){
		  HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//p[@class='result-info']/a"));

		  HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']")) ;
							
		  String itemName = itemAnchor.asText();
		  String itemUrl =  itemAnchor.getHrefAttribute();

		  // It is possible that an item doesn't have any price
		  String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText() ;
					
		  System.out.println( String.format("Name : %s Url : %s Price : %s", itemName, itemPrice, itemUrl));
		  }
		}
	}
	
	
	
	public Mono<ResponseEntity<String>> run()
	{
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		  body.set("name 1", "value 1");
		  body.add("name 2", "value 2+1");
		  body.add("name 2", "value 2+2");
		  body.add("name 3", null);
		String url="projects/{PROJECT_TOKEN}/run";
		Mono<ResponseEntity<String>> res= webClientServiceBroker.post(url, body);
		return res;		
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
    	cell1.setCellValue((String) "PAGE_CODE"); 
    	Cell cell2 = row.createCell(2);
    	cell2.setCellValue((String) "ISO");
    	Cell cell3 = row.createCell(3);
    	cell3.setCellValue((String) "TITLE");
    	Cell cell4 = row.createCell(4);
    	cell4.setCellValue((String) "P_STATUS"); 
    	Cell cell5 = row.createCell(5);
    	cell5.setCellValue((String) "P_DATE");
    	Cell cell6 = row.createCell(6);
    	cell6.setCellValue((String) "EDITION");
    	Cell cell7 = row.createCell(7);
    	cell7.setCellValue((String) "No.Pages");
    	Cell cell8 = row.createCell(8);
    	cell8.setCellValue((String)"T_C_CODE");
    	Cell cell9 = row.createCell(9);
    	cell9.setCellValue((String) "T_C_DETAILS");
    	Cell cell10 = row.createCell(10);
    	cell10.setCellValue((String) "ICS_CODES");
    	Cell cell11 = row.createCell(11);
    	cell11.setCellValue((String) "ICS_LINKS");
    	Cell cell12 = row.createCell(12);
    	cell12.setCellValue((String) "ICS_DETAILS");
    	Cell cell13 = row.createCell(13);
    	cell13.setCellValue((String) "L_CODE");
    	Cell cell14 = row.createCell(14);
    	cell14.setCellValue((String) "L_STATUS");	
    	Cell cell15 = row.createCell(15);
    	cell15.setCellValue((String) "L_CODE_LIST");
    	Cell cell16 = row.createCell(16);
    	cell16.setCellValue((String) "L_TITLE_LIST");
    	Cell cell17 = row.createCell(17);
    	cell17.setCellValue((String) "L_DETAIL_LIST");
    	Cell cell18 = row.createCell(18);
    	cell18.setCellValue((String) "REVISED_BY");
    	Cell cell19 = row.createCell(19);
    	cell19.setCellValue((String) "PRICE");
    	Cell cell20 = row.createCell(20);
    	cell20.setCellValue((String) "PREVIOUSLY");
    	Cell cell21 = row.createCell(21);
    	cell21.setCellValue((String) "NOW_");
    	Cell cell22 = row.createCell(22);
    	cell22.setCellValue((String) "ABSTRACT");
    	Cell cell23 = row.createCell(23);
    	cell23.setCellValue((String) "ISO_URL");
    	Cell cell24 = row.createCell(24);
    	cell24.setCellValue((String) "ICS_FULL");
	}
	
}
