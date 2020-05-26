package com.akabana.AntonioliWebScraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntonioliWebSiteScraper {
	
	private String webSiteLink = ""; //main website url
	private String proxyHost = "";
	private String proxyPort= "";
	private String proxyUser = "";
	private String proxyPassword = "";
	private List<String> itemsLinks;  //list of product webpages that generated antonioli items list
	private List<String> itemsSkippedLinks; //listo f product webpages skipped due to a some error during webscraping
	private List<AntonioliItem> antonioliItems; //list of items retrivied (one for each size found)
	private Logger logger;
	
    public AntonioliWebSiteScraper(String webSiteLink) throws Exception {
    	this.webSiteLink = webSiteLink;
    	this.itemsLinks = new ArrayList<String>();
    	this.itemsSkippedLinks = new ArrayList<String>();
    	this.antonioliItems = new ArrayList<AntonioliItem>();
    }
    
    public AntonioliWebSiteScraper(String webSiteLink, Logger logger) throws Exception {
    	this(webSiteLink);
    	this.logger = logger;    	
    }
    
    /***
     * 
     * @param webSiteLink
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser empty string if no credentials are required
     * @param proxyPassword empty string if no credentials are required
     * @throws Exception
     */
    public AntonioliWebSiteScraper(String webSiteLink, String proxyHost, String proxyPort, String proxyUser, String proxyPassword) throws Exception {
    	this(webSiteLink);
    	this.proxyHost = proxyHost;
    	this.proxyPort = proxyPort;
    	this.proxyUser = proxyUser;
    	this.proxyPassword = proxyPassword;
    }
    
    /***
     * 
     * @param webSiteLink
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser empty string if no credentials are required
     * @param proxyPassword empty string if no credentials are required
     * @param logger
     * @throws Exception
     */
    public AntonioliWebSiteScraper(String webSiteLink, String proxyHost, String proxyPort, String proxyUser, String proxyPassword, Logger logger) throws Exception {
    	this(webSiteLink, proxyHost, proxyPort, proxyUser, proxyPassword);
    	this.logger = logger;
    }
    
    /***
     * Uses the category url provided, concatenated to the web site main url, and get all the products web pages urls available.
     * The variant is a smart one, use the list of Antonioli Items provided as parameter to skip the scrap of items where
     * update is not requested; in that case the item is retrivied from the list provided. 
     * The webpage link is added to the itemsLinks list;
     * all the items retrieved are returned in a list of AntonioliItem objects.
     * @param URL
     * @param iteratePages
     * @param avoidScrapeItems
     * @return
     * @throws Exception
     */
    public List<AntonioliItem> scrapeCategoryWebPageSmart(String URL, boolean iteratePages, List<AntonioliItem> avoidScrapeItems) throws Exception
    {
    	if(logger!=null)
			logger.log(Level.FINE, "Start scrapeCategoryWebPageSmart method");
    	try 
    	{
    		//get all the product pages links available in the page category provided
    		List<String> returnedLinks = getItemsLinks(URL, iteratePages);
    		
    		if(logger!=null)
    			logger.log(Level.INFO, "Got from the category pages "+URL+" "+returnedLinks.size()+" links");
    		boolean itemNoteBeScraped = false;
    		if(returnedLinks.size() > 0)
	    	{
    			for(int i=0;i<returnedLinks.size();i++)
	    		{
    				itemNoteBeScraped = false;
	    			//look if the item is to be not scraped no update needed
	    			for(AntonioliItem item : avoidScrapeItems)
	    			{
	    				if(item.getItemLink().equals(returnedLinks.get(i)))
	    				{
	    					if(logger!=null)
	    		    			logger.log(Level.FINE, "Item link "+returnedLinks.get(i)+" no needed to be scraped.");
	    					if(!antonioliItems.contains(item))
	    					{
	    						if(logger!=null)
	    		        			logger.log(Level.INFO, "Add item "+item.getItemID()+" to the list, got object from the list avoided.");
	    						antonioliItems.add(item);
	    					}
	    					else
	    					{
	    						if(logger!=null)
	    		        			logger.log(Level.INFO, "Item "+item.getItemID()+" already present in the list");
	    					}	    			
	    					itemNoteBeScraped = true;
	    				}//if to be not scraped
	    			}//for
	    			
	    			if(!itemNoteBeScraped)
    				{
	    				List<AntonioliItem> returnedItems;
	    				
	    				if(logger!=null)
		        			logger.log(Level.INFO, "Item needed to be scraped.");
	    				
    					//get the list of items available in the product page (an item per size)
    	    			returnedItems = getItemsFromLink(returnedLinks.get(i));
    	    			
    	    			if(logger!=null)
    	        			logger.log(Level.INFO, "Got from the item page "+returnedLinks.get(i)+" "+returnedItems.size()+" items");
    	    			//iterate on returned list of items and add them to the final result list
    	    			if(returnedItems.size() > 0)
    	    			{
    	    				//each AntonioliItem is added to the List antonioliItems (if not already exists)
    	    				for(int j=0;j<returnedItems.size();j++)
    	    				{
    	    					if(!antonioliItems.contains(returnedItems.get(j)))
    	    					{
    	    						if(logger!=null)
    	    		        			logger.log(Level.INFO, "Add item "+returnedItems.get(j).getItemID()+" to the list");
    	    						antonioliItems.add(returnedItems.get(j));
    	    					}
    	    					else
    	    					{
    	    						if(logger!=null)
    	    		        			logger.log(Level.INFO, "Item "+returnedItems.get(j).getItemID()+" already present in the list");
    	    					}	
    	    				}//for	    				
    	    			}//if
    	    		}//else needed to be scraped
	    				
    				//complete the list of product page links
    				if(!itemsLinks.contains(returnedLinks.get(i)))
	    			{
	    				if(logger!=null)
		        			logger.log(Level.INFO, "Add link "+returnedLinks.get(i)+" to the list");
	    				itemsLinks.add(returnedLinks.get(i));
	    			}//if
	    			else
	    			{
						if(logger!=null)
		        			logger.log(Level.INFO, "Item "+returnedLinks.get(i)+" already present in the list");
					}//else		    					    			
	    		}//for on returnedLinks from category page
	    	}//if
    		
    		if(logger!=null)
    			logger.log(Level.FINE, "End scrapeCategoryWebPageSmart method");
    		
    		return antonioliItems;
    	}
    	catch (Exception e) 
		{
            throw new Exception(e.getMessage());
        } 
    }
    
    /**
     * Uses the category url provided, concatenated to the web site main url, and get all the products web pages urls available.
     * If from each product page, items are retrieved (one per size), the webpage link is added to the itemsLinks list;
     * all the items retrieved are returned in a list of AntonioliItem objects.
     * @param URL category page sub url, with no main website address
     * @param iteratePages if true pages 1, 2 ,3, ... are scanned until no items found
     * @return the updated List antonioliItems
     * @throws Exception
     */
    public List<AntonioliItem> scrapeCategoryWebPage(String URL, boolean iteratePages) throws Exception
    {
    	if(logger!=null)
			logger.log(Level.FINE, "Start scrapeCategoryWebPage method");
    	try 
    	{
    		//get all the product pages links available in the page category provided
    		List<String> returnedLinks = getItemsLinks(URL, iteratePages);
    		
    		if(logger!=null)
    			logger.log(Level.INFO, "Got from the category pages "+URL+" "+returnedLinks.size()+" links");
    		
    		if(returnedLinks.size() > 0)
	    	{
    			List<AntonioliItem> returnedItems;
    			//for each link get the list of AntonioliItem items (one per available size)
	    		for(int i=0;i<returnedLinks.size();i++)
	    		{
	    			//get the list of items available in the product page (an item per size)
	    			returnedItems = getItemsFromLink(returnedLinks.get(i));
	    			
	    			if(logger!=null)
	        			logger.log(Level.INFO, "Got from the item page "+returnedLinks.get(i)+" "+returnedItems.size()+" items");
	    			//iterate on returned list of items and add them to the final result list
	    			if(returnedItems.size() > 0)
	    			{
	    				//each AntonioliItem is added to the List antonioliItems (if not already exists)
	    				for(int j=0;j<returnedItems.size();j++)
	    				{
	    					if(!antonioliItems.contains(returnedItems.get(j)))
	    					{
	    						if(logger!=null)
	    		        			logger.log(Level.INFO, "Add item "+returnedItems.get(j).getItemID()+" to the list");
	    						antonioliItems.add(returnedItems.get(j));
	    					}
	    					else
	    					{
	    						if(logger!=null)
	    		        			logger.log(Level.INFO, "Item "+returnedItems.get(j).getItemID()+" already present in the list");
	    					}	
	    				}//for
	    				
	    				//complete the list of product page links
	    				if(!itemsLinks.contains(returnedLinks.get(i)))
		    			{
		    				if(logger!=null)
			        			logger.log(Level.INFO, "Add link "+returnedLinks.get(i)+" to the list");
		    				itemsLinks.add(returnedLinks.get(i));
		    			}//if
		    			else
		    			{
							if(logger!=null)
			        			logger.log(Level.INFO, "Item "+returnedLinks.get(i)+" already present in the list");
						}//else		    				
	    			}//if	    			
	    		}//for	    		
	    	}//if   
	    	
    		if(logger!=null)
    			logger.log(Level.FINE, "End scrapeCategoryWebPage method");
    		
	    	return antonioliItems;
    	} 
		catch (Exception e) 
		{
            throw new Exception(e.getMessage());
        }    	
    }//scrapeCategoryWebPage
    
    /**
     * Scans the category page (url provided concatenated to the WebSite Main url) and
     * get all the URLs pointing to the items pages.  
     * @param URL category page sub url, with no main website address
     * @param iteratePages if true pages 1, 2 ,3, ... are scanned until no items found
     * @return List of link retrieved from the category page
     * @throws Exception
     */
    private List<String> getItemsLinks(String URL, boolean iteratePages) throws Exception 
    {
    	if(logger!=null)
			logger.log(Level.FINE, "Start getItemsLinks method");
    	boolean itemsAvailable = true;
    	String newUrl = webSiteLink + URL + "&page=1";
    	int iterateStep = 1;
    	WebClient webClient = StartWebBrowser();
    	HtmlPage page = null;
    	List<String> returnList = new ArrayList<String>();
    	
    	try 
    	{
			while(itemsAvailable)
    		{
    			//while the page have items iterate
				
    			if(logger!=null)
					logger.log(Level.FINE, "Open the web page: "+newUrl);
				boolean makeTentative = true;
				int numTentative = 1;
				while(makeTentative)
				{
					try
					{
						page = webClient.getPage(newUrl);
						webClient.waitForBackgroundJavaScript(10000);
						makeTentative = false;
					}
					catch (Exception e)
					{
						logger.log(Level.WARNING, "The category page "+newUrl+" generated an error, retry.");
						logger.log(Level.FINE, "Close web browser");
						webClient.getCurrentWindow().getJobManager().removeAllJobs();
				        webClient.close();
				        webClient = StartWebBrowser();
					}
					numTentative++;
					if(numTentative==4)
					{
						makeTentative = false;
						logger.log(Level.SEVERE, "Cannot get the category page "+newUrl+" after 3 tenatives, the download is compromised. Better to stop.");
						throw new Exception("Cannot get the category page "+newUrl+" after 3 tenatives.");
					}
					Thread.sleep(5000);
				}				
										    				    		
	    		//get all the article html elements
	    		if(logger!=null)
    				logger.log(Level.FINE, "Look for article html elements");
	    		DomNodeList<DomElement> itemsElementList = page.getElementsByTagName("article");
	    		
	    		//each page has always one element article to be not considered
	    		if(itemsElementList.size() <= 1)
	    		{
	    			//the page has no article, there are no other pages of articles
	    			if(logger!=null)
	    				logger.log(Level.FINE, "No more articles found in the page, stop the iterate on pages");
	    			break;
	    		}
	    		else
	    		{
	    			//iterate on elements article from the 2nd one
	    			if(logger!=null)
	    				logger.log(Level.FINE, "Look for a html elements with the article element");
		    		for(int i = 1; i<itemsElementList.size(); i++)
		    		{
		    			DomNodeList<HtmlElement> aElement = (itemsElementList.get(i)).getElementsByTagName("a");
		    			if(aElement.size()!=0)
		    			{
		    				if(logger!=null)
			    				logger.log(Level.FINE, "Found article page link: "+ webSiteLink + ((HtmlElement)aElement.get(0)).getAttribute("href"));
		    				returnList.add(webSiteLink + ((HtmlElement)aElement.get(0)).getAttribute("href"));	
		    			}
		    		}
		    		//create the next page url
		    		if(iteratePages)
		    		{
			    		iterateStep++;
			    		newUrl = webSiteLink + URL + "&page="+Integer.toString(iterateStep);
		    		}
		    		else
		    			itemsAvailable = false;
	    		}         
    		}
			
    		if(logger!=null)
    			logger.log(Level.FINE, "End getItemsLinks method");
	    	
    		return returnList;
        } 
		catch (Exception e) 
		{
            throw new Exception(e.getMessage());
        }  
    	finally 
    	{
    		if(webClient != null)
    		{
	            webClient.getCurrentWindow().getJobManager().removeAllJobs();
	            webClient.close();
	            System.gc();
    		}
        }
    }//getItemsLinks
    
    /**
     * Scans the item page (url provided concatenated to the WebSite Main url) and for each item size
     * creates an AntonioliItem object.
     * @param itemLink url to the item page
     * @return List of AntonioliItem created from the item page
     * @throws Exception
     */
    private List<AntonioliItem> getItemsFromLink(String itemLink) throws Exception
    {
    	if(logger!=null)
    		logger.log(Level.FINE, "Start getItemFromLink method");
    	
    	String name = "";
    	String brand = "";
    	String style = "";
    	String color = "";
    	String sku = "";
    	String description = "";
    	List<String> sizes = new ArrayList<String>();
    	String price = "";
    	String priceCurrency = "";
    	String availability = "";
    	String category = "";
    	String[] categories;
    	String gender = "";
    	String hierarchy1 = "";
    	String hierarchy2 = ""; //used to store the size chart
    	String season = "";
    	String picture = "";
    	String pictureAlt1 = "";
    	String pictureAlt2 = "";
    	String pictureAlt3 = "";
    	String pictureAlt4 = "";
    	
    	List<AntonioliItem> returnItems = new ArrayList<AntonioliItem>();
    	AntonioliItem antonioliItem;
    	WebClient webClient = null;
    	HtmlPage page = null;
    	
    	try
    	{    		
    		//open the web client
			webClient = StartWebBrowser();
			
			//open the web page
			if(logger!=null)
				logger.log(Level.FINE, "Open the web page: "+itemLink);
			
			page = webClient.getPage(itemLink);
			webClient.waitForBackgroundJavaScript(10000);
						
    		//get the article html section
    		if(logger!=null)
				logger.log(Level.FINE, "Look for article html elements");
    		DomNodeList<DomElement> itemsElementList = page.getElementsByTagName("article");
    		
    		//the page has always one element article to be not considered
    		if(itemsElementList.size() > 1)
    		{
    			if(logger!=null)
    				logger.log(Level.FINE, "Look for aside html elements");
    			DomNodeList<HtmlElement> asideElement = (itemsElementList.get(1)).getElementsByTagName("aside");
    			
    			if(asideElement.size()>0)
    			{
    				if(logger!=null)
        				logger.log(Level.FINE, "Look for html element product-details class");
    				HtmlElement productDetails = (asideElement.get(0)).getFirstByXPath("//div[@class='product-details']");
    				
    				//left part of page: product description
    				
    				if(productDetails != null)
    				{
    					if(logger!=null)
	        				logger.log(Level.FINE, "Look for html element h2");
	    				DomNodeList<HtmlElement> h2 = productDetails.getElementsByTagName("h2");
	    				//h2 an h3 have not always an a element to get the gender
	    				if(h2.size() > 0)
	    				{
	    					if(logger!=null)
	            				logger.log(Level.FINE, "Look for html element a inside h2");
	    					DomNodeList<HtmlElement> a = (h2.get(0)).getElementsByTagName("a");
	    					if(a.size() > 0)
	    					{
	    						categories = (a.get(0)).getAttribute("href").split("/");
	    						gender = categories[3].equals("men") ? "M": "W";
	    						//category = categories[6];
	    						//hierarchy1 = categories[6];
	    					}
	    				}
	    				
	    				if(logger!=null)
	        				logger.log(Level.FINE, "Look for html element h3");
	    				DomNodeList<HtmlElement> h3 = productDetails.getElementsByTagName("h3");
    				
	    				if(h3.size() > 0)
	    				{
	    					if(logger!=null)
	            				logger.log(Level.FINE, "Look for html element a inside h3");
	    					DomNodeList<HtmlElement> a = (h3.get(0)).getElementsByTagName("a");
	    					if(a.size() > 0)
	    					{
	    						categories = (a.get(0)).getAttribute("href").split("/");
	    						gender = categories[3].equals("men") ? "M": "W";
	    						//category = categories[6];
	    						hierarchy1 = categories[6];
	    					}
	    				}
	    				
	    				if(logger!=null)
	        				logger.log(Level.FINE, "Look for html element product-description class");
	    				HtmlElement productDescription = productDetails.getFirstByXPath("//div[@class='product-description']");
	    				
		    				if(productDescription != null)
		    				{
			    				DomNodeList<HtmlElement> productContents = productDescription.getElementsByTagName("p");
			    				if(productContents.size() > 0)
			    				{
				    				for(HtmlElement productContent : productContents)
				    				{
				    					if(logger!=null)
				            				logger.log(Level.FINE, "Manage html element p, property itemprop: "+productContent.getAttribute("itemprop"));
				    					
				    					if(productContent.getAttribute("itemprop").equals("name"))
				    						name = (productContent.getAttribute("content")).replaceAll("\u00A0","");
				    					if(productContent.getAttribute("itemprop").equals("brand"))
				    						brand = (productContent.getAttribute("content")).replaceAll("\u00A0","");
				    					if(productContent.getAttribute("itemprop").equals("model"))
				    					{
				    						style = (((productContent.getAttribute("content")).replaceAll("\u00A0","")).split(" "))[0];
				    						if((productContent.getAttribute("content")).split(" ").length > 1)
				    							color = (((productContent.getAttribute("content")).replaceAll("\u00A0","")).split(" "))[1];
				    					}
				    					if(productContent.getAttribute("itemprop").equals("sku"))
				    						sku = (productContent.getAttribute("content")).replaceAll("\u00A0","");
				    					if(productContent.getAttribute("itemprop").equals("description"))
				    						description = (productContent.getAttribute("content")).replaceAll("\u00A0","");
				    				}//for
			    				}//if
		    				DomNodeList<HtmlElement> productSizeDescs = productDescription.getElementsByTagName("dd");
		    				if(productSizeDescs.size() > 0)
		    				{
		    					for(HtmlElement productSizeDesc : productSizeDescs)
			    				{
		    						if(productSizeDesc.getId().equals("size-and-fit"))
		    						{
		    							if(logger!=null)
				            				logger.log(Level.FINE, "Found html element dd with id size-and-fit to get the size desctiption");
		    							DomNodeList<HtmlElement> productSizeDescDivs = productSizeDesc.getElementsByTagName("div");
		    							if(productSizeDescDivs.size() > 0)
		    							{
		    								if(logger!=null)
					            				logger.log(Level.FINE, "Found html element div within dd with id size-and-fit to get the size desctiption");
		    								for(HtmlElement productSizeDescDiv : productSizeDescDivs)
		    								{
		    									description = description + "\n" + (productSizeDescDiv.getTextContent()).replaceAll("\u00A0","");
		    								}//for
		    							}//if
		    						}//if
			    				}//for
		    				}//if		    				
		    			}//if div[@class='product-details'] html element exits
    				}//if
    				
    				//right part of page: price, availability, sizes
    				if(logger!=null)
        				logger.log(Level.FINE, "Look for html element box-add class");
    				HtmlElement boxAddClass = (asideElement.get(0)).getFirstByXPath("//div[contains(@class, 'box-add')]");
    				if(boxAddClass != null)
    				{
	    				if(logger!=null)
	        				logger.log(Level.FINE, "Look for header html element");
	    				DomNodeList<HtmlElement> boxAddHeader = boxAddClass.getElementsByTagName("header");
	    				if(boxAddHeader.size() > 0)
	    				{
	    					DomNodeList<HtmlElement> spans = boxAddClass.getElementsByTagName("span");
	    					if(spans.size() > 0)
	    					{
	    						for(HtmlElement span : spans)
	    	    				{
	    							if(span.getAttribute("itemprop").equals("price"))
	    								price = (span.getAttribute("content")).replaceAll("\u00A0","");
	    							if(span.getAttribute("itemprop").equals("priceCurrency"))
	    								priceCurrency = (span.getAttribute("content")).replaceAll("\u00A0","");
	    	    				}//for
	    					}//if
	    				}//if
	    				if(logger!=null)
	        				logger.log(Level.FINE, "Look for html element having class product-cart-form js-cart-form");
	    				HtmlElement productCartForm = (asideElement.get(0)).getFirstByXPath("//form[@class='product-cart-form js-cart-form']");
	    				if(productCartForm != null)
	    				{
		    				if(logger!=null)
		        				logger.log(Level.FINE, "Look for html element having class availability available_now");
		    				HtmlElement availabilityDiv = productCartForm.getFirstByXPath("//div[contains(@class, 'availability')]");
		    				if(availabilityDiv != null)
		    					availability = (availabilityDiv.getFirstChild().getNodeValue()).replaceAll("\u00A0","");
		    				
		    				if(logger!=null)
		        				logger.log(Level.FINE, "Look for html element having class select-variant");
		    				HtmlElement variants = productCartForm.getFirstByXPath("//div[@class='select-variant']");
		    				if(variants != null)
		    				{
			    				
			    				if(logger!=null)
			        				logger.log(Level.FINE, "Look for html element div (product-variants) inside element having class preorder-product");
			    				HtmlElement variantDiv = productCartForm.getFirstByXPath("//div[@class='product-variants']");
			    				if(logger!=null)
			        				logger.log(Level.FINE, "Look for html element label");
			    				if(variantDiv != null)
			    				{
									DomNodeList<HtmlElement> labels = variantDiv.getElementsByTagName("label");
									for(HtmlElement label :labels)
									{
										sizes.add(label.getFirstChild().getNodeValue().replaceAll("\n", ""));
									}  
			    				}//if
			    				
		    				}//if div[@class='select-variant'] html element exists
		    				
		    				//get size chart
		    				if(logger!=null)
		        				logger.log(Level.FINE, "Look for html element having class size-chart-link");
		    				HtmlElement sizeCharts = productCartForm.getFirstByXPath("//div[@class='size-chart-link']");
		    				if(sizeCharts != null)
		    				{
		    					if(logger!=null)
			        				logger.log(Level.FINE, "Look for html element a inside element having class size-chart-link");
		    					HtmlElement sizeChart = sizeCharts.getFirstByXPath("//a[@class='link-chart js-chart']");
		    					if(sizeChart != null)
		    						hierarchy2 = (sizeChart.getTextContent()).replaceAll("\u00A0","");
		    					
		    				}
	    				}//if form[@class='product-cart-form js-cart-form'] html element exists
    				}//if div[contains(@class, 'box-add')] html element exists    				
    			}//if aside html element exists  
    			
    			//pictures gallery
    			if(logger!=null)
    				logger.log(Level.FINE, "Look for html element div element having class owl-stage");
    			HtmlElement galleryOwlTheme = (itemsElementList.get(1)).getFirstByXPath("//div[@class='gallery owl-theme owl-carousel']");
    			if(galleryOwlTheme != null)
    			{
    				if(logger!=null)
        				logger.log(Level.FINE, "Look for html element div inside element having class owl-stage");
    				DomNodeList<HtmlElement> owlItems = galleryOwlTheme.getElementsByTagName("div");
    				if(owlItems.size() > 0)
    				{
    					if(logger!=null)
            				logger.log(Level.FINE, "Look for html element div element having class owl-item active pr owl-item");
    					
    					for(int i=0;i<owlItems.size();i++)
	    				{
    						//HtmlElement owlItem : owlItems
							if(((HtmlElement)owlItems.get(i)).getAttribute("class").equals("item"))
							{
								if(logger!=null)
		            				logger.log(Level.FINE, "Look for html element img");
								DomNodeList<HtmlElement> imgs = ((HtmlElement)owlItems.get(i)).getElementsByTagName("img");
								if(imgs.size()>0)
								{
									switch(i)
									{
										case 0:
											picture = (imgs.get(0).getAttribute("src")).replaceAll("\u00A0","");
											break;
										case 1:
											pictureAlt1 = (imgs.get(0).getAttribute("src")).replaceAll("\u00A0","");
											break;
										case 2:
											pictureAlt2 = (imgs.get(0).getAttribute("src")).replaceAll("\u00A0","");
											break;
										case 3:
											pictureAlt3 = (imgs.get(0).getAttribute("src")).replaceAll("\u00A0","");
											break;
										case 4:
											pictureAlt4 = (imgs.get(0).getAttribute("src")).replaceAll("\u00A0","");
											break;
									}
								}//if
							}//if
	    				}//for
    				}//if div class owl-item exists   				
    			}//if div[@class='owl-stage'] html element exists    			
    		}//if article html element exits
    		
    		
    		//article footer part: get the season
    		if(logger!=null)
				logger.log(Level.FINE, "Look for footer html elements");
    		DomNodeList<DomElement> footer = page.getElementsByTagName("footer");
    		
    		if(footer.size() > 0)
    		{
    			if(logger!=null)
    				logger.log(Level.FINE, "Look for section html elements having class similar-items");
    			HtmlElement similarItems = (footer.get(0)).getFirstByXPath("//section[@class='similar-items']");
    			if(similarItems != null)
    			{
	    			if(logger!=null)
	    				logger.log(Level.FINE, "Look for section html elements a within html element having class class similar-items");
	    			DomNodeList<HtmlElement> as = similarItems.getElementsByTagName("a");
	    			for(HtmlElement a : as)
					{
	    				if(a.getAttribute("href").contains("seasons"))
	    					season = (a.getAttribute("title")).replaceAll("\u00A0","");
	    				if(a.getAttribute("href").contains("genders"))
	    					gender = ((a.getAttribute("title")).replaceAll("\u00A0","")).equals("Female") ? "W" : "M";
	    				if(a.getAttribute("href").contains("categories"))
	    					category = (a.getAttribute("title")).replaceAll("\u00A0","");
					}//for
    			}//if section[@class='similar-items'] html element exists
    		}//if
    		
    		if(logger!=null)
				logger.log(Level.FINE, "Create now an item object for each size.");
			
    		//create the items object and add it to the list
    		for(int i=0;i<sizes.size();i++)
    		{
    			if(logger!=null)
    				logger.log(Level.FINE, "Create object for item: "+sku+", size: "+sizes.get(i));
    			
    			antonioliItem = new AntonioliItem();
    			antonioliItem.setItemID("ANTONIOLI-"+gender+"-"+season+"-"+sku.replace("\\", ""));
    			antonioliItem.setItemSku(sku);
    			antonioliItem.setItemBrand(brand);
    			antonioliItem.setItemStyle(style);
    			antonioliItem.setItemColor(color);
    			antonioliItem.setItemGender(gender);
    			antonioliItem.setItemDescription(description);
    			antonioliItem.setItemAvailability(availability);
    			antonioliItem.setItemCategory(category);
    			antonioliItem.setItemHierarchy1(hierarchy1);
    			antonioliItem.setItemHierarchy2(hierarchy2);
    			antonioliItem.setItemLink(itemLink);
    			antonioliItem.setItemName(name);
    			antonioliItem.setItemPrice(price);
    			antonioliItem.setItemPriceCurrency(priceCurrency);
    			antonioliItem.setItemSeason(season);
    			antonioliItem.setItemSize(sizes.get(i));
    			antonioliItem.setItemPicture(picture);
    			antonioliItem.setItemPictureAlt1(pictureAlt1);
    			antonioliItem.setItemPictureAlt2(pictureAlt2);
    			antonioliItem.setItemPictureAlt3(pictureAlt3);
    			antonioliItem.setItemPictureAlt4(pictureAlt4);
    			returnItems.add(antonioliItem);
    		}//for
    		
			if(logger!=null)
	    		logger.log(Level.FINE, "End getItemFromLink method");
			
			return returnItems;
    	}//try
    	catch (Exception e) 
		{
    		if(logger!=null)
				logger.log(Level.WARNING, "An error occurred with the page: "+itemLink+"; skip items. Error: "+e.getMessage());
			//if an error occurred skip the product page, add it to the skipped pages and go on
			this.itemsSkippedLinks.add(itemLink);
			return returnItems;
        }  
    	finally 
    	{
    		if(webClient != null)
    		{
	            webClient.getCurrentWindow().getJobManager().removeAllJobs();
	            webClient.close();
	            System.gc();
    		}
        }
    }//getItemsFromLink
    
    private WebClient StartWebBrowser() throws Exception
    {
    	try
    	{
	    	//light web browser start
			if(logger!=null)
				logger.log(Level.FINE, "Start the light Web Browser");
			final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
			
			webClient.getCookieManager().setCookiesEnabled(true);
	        webClient.getOptions().setJavaScriptEnabled(true);
	        webClient.getOptions().setTimeout(60000);
	        webClient.getOptions().setCssEnabled(true);
	        webClient.getOptions().setThrowExceptionOnScriptError(false);
	        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
	        webClient.setCssErrorHandler(new SilentCssErrorHandler());
	        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
	        webClient.getOptions().setPopupBlockerEnabled(true);
	        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	        webClient.getCache().setMaxSize(0);
	        //webClient.getOptions().setRedirectEnabled(false);
	        
	        //web proxy?
	        if(!this.proxyHost.equals(""))
	        {
	        	ProxyConfig proxyConfig = new ProxyConfig(this.proxyHost, Integer.parseInt(this.proxyPort));
	        	webClient.getOptions().setProxyConfig(proxyConfig);
	        	if(!this.proxyUser.equals(""))
	        	{
	        		final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
	        		credentialsProvider.addCredentials(this.proxyUser, this.proxyPassword);	        		
	        	}
	        }
	        
	        final History window = webClient.getWebWindows().get(0).getHistory();
	        final Field f = window.getClass().getDeclaredField("ignoreNewPages_"); //NoSuchFieldException
	        f.setAccessible(true);
	        ((ThreadLocal<Boolean>) f.get(window)).set(Boolean.TRUE);
	        
	        try
	        {
		        TextPage page = webClient.getPage("https://api.ipify.org/");
		        if(logger!=null)
		    		logger.log(Level.INFO, "Light browser started. IP: "+ page.getContent());       
	        }
	        catch (Exception e) 
			{
	        	if(logger!=null)
					logger.log(Level.WARNING, "Could not get the current public IP.");
	        }
	        /*
	        String proxyname = "guess.proxy.eu";
	        int proxyport = 8080;
	        ProxyConfig proxy = new ProxyConfig(proxyname,proxyport);
	        webClient.getOptions().setProxyConfig(proxy);
	        
	        final DefaultCredentialsProvider cp = new DefaultCredentialsProvider();
	        cp.addCredentials("GUESSEU\tmorello", "password",proxyname,proxyport,null);
	        webClient.setCredentialsProvider(cp);
	        */
	        
	        return webClient;
    	}//try
        catch (Exception e) 
		{
            throw new Exception(e.getMessage());
        }
    }//StartWebBrowser
    
    /**
     * Gets the html page from: the web main url concatenated to the one in the argument, and download it to text a file. 
     * @param url to be concatenated to the main web site link
     * @param FilepathFilename path and name of destination file
     * @throws Exception
     */
    public void getPageXml(String url, String FilepathFilename) throws Exception
    {
    	//open the web client
    	WebClient webClient = StartWebBrowser();
    	HtmlPage page = null;
    	String html = null;
    	BufferedWriter writer = null;
    	
    	try
    	{
    		page = webClient.getPage(webSiteLink+url);
			webClient.waitForBackgroundJavaScript(6000);
			html = page.asXml();
			writer = new BufferedWriter(new FileWriter(FilepathFilename));
		    writer.write(html);		     
		    writer.close();
    	}//try
    	catch (Exception e) 
		{
            throw new Exception(e.getMessage());
        }
    	finally 
    	{
    		if(webClient != null)
    		{
	            webClient.getCurrentWindow().getJobManager().removeAllJobs();
	            webClient.close();
	            writer.close();
	            System.gc();
    		}
        }//finally
    }//getPageXml
    
    /***
     * Get the list of web pages links that generated the list of antonioli items
     * @return
     */
    public List<String> getItemsLinkList()
    {
    	return this.itemsLinks;
    }   
    
    /***
     * Get the list of product webpages skipped sue to an error
     * @return
     */
    public List<String> getSkippedItemsLinks()
    {
    	return this.itemsSkippedLinks;
    } 
    
    public String getWebSiteLink()
    {
    	return this.webSiteLink;
    }
    
    /***
     * Get the  list of AntonioliItems objects scraped
     * @return
     */
    public List<AntonioliItem> getAllAntonioliItems()
    {
    	return this.antonioliItems;
    }
    
    public void setWebSiteLink(String webSiteLink)
    {
    	this.webSiteLink = webSiteLink;
    }
}