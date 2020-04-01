package com.akabana.AntonioliWebScraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.History;
//import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntonioliWebSiteScraper {
	
	private String webSiteLink;
	private List<String> itemsLinks;
	private List<AntonioliItem> antonioliItems;
	private Logger logger;
	//private final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
	
    public AntonioliWebSiteScraper(String webSiteLink) throws Exception {
    	this.webSiteLink = webSiteLink;
    	this.itemsLinks = new ArrayList<String>();
    	this.antonioliItems = new ArrayList<AntonioliItem>();
    }
    
    public AntonioliWebSiteScraper(String webSiteLink, Logger logger) throws Exception {
    	this(webSiteLink);
    	this.logger = logger;    	
    }
    
    /**
     * Uses the category url provided, concatenated to the web site main url, get all the items page links
     * and, if it doesn't exist in the List itemsLinks, adds the link to the list; for each link the AntonioliItem objects
     * retrieved (one per available size) is added (if it is not stored already) to the antonioliItems List.
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
    		/*
    		if(logger!=null)
    			logger.log(Level.INFO, "Start the ligh Web Browser");
    		//open the web page
			StartWebBrowser();
			*/
    		
    		//get all the links available in the page category provided
    		List<String> returnedLinks = getItemsLinks(URL, iteratePages);
    		
    		if(logger!=null)
    			logger.log(Level.INFO, "Got from the category pages "+URL+" "+returnedLinks.size()+" links");
    		
    		if(returnedLinks.size() > 0)
	    	{
    			List<AntonioliItem> returnedItems;
    			//for each link get the list of AntonioliItem (one per available size)
	    		for(int i=0;i<returnedLinks.size();i++)
	    		{
	    			returnedItems = getItemsFromLink(returnedLinks.get(i));
	    			if(logger!=null)
	        			logger.log(Level.INFO, "Got from the item page "+returnedLinks.get(i)+" "+returnedItems.size()+" items");
	    			if(returnedItems.size() > 0)
	    			{
	    				//each AntonioliItem is added to the List antonioliItems
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
	    		        			logger.log(Level.INFO, "Item "+returnedItems.get(j).getItemID()+" alredy present in the list");
	    					}	
	    				}//for
	    				
	    				if(!itemsLinks.contains(returnedLinks.get(i)))
		    			{
		    				if(logger!=null)
			        			logger.log(Level.INFO, "Add link "+returnedLinks.get(i)+" to the list");
		    				itemsLinks.add(returnedLinks.get(i));
		    			}//if
		    			else
		    			{
							if(logger!=null)
			        			logger.log(Level.INFO, "Item "+returnedLinks.get(i)+" alredy present in the list");
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
    	WebClient webClient = null;
    	HtmlPage page = null;
    	List<String> returnList = new ArrayList<String>();
    	
    	try 
    	{
			while(itemsAvailable)
    		{
    			//while the page have items iterate
				
    			//open the web client
				webClient = StartWebBrowser();
				
				if(logger!=null)
					logger.log(Level.FINE, "Open the web page: "+newUrl);
				
				try
				{
					page = webClient.getPage(newUrl);
					webClient.waitForBackgroundJavaScript(6000);
				}
				catch (Exception e)
				{
					//if an error occurred skip the item
					return returnList;
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
			
			try
			{
				page = webClient.getPage(itemLink);
				webClient.waitForBackgroundJavaScript(6000);
			}
			catch (Exception ex)
			{
				if(logger!=null)
					logger.log(Level.WARNING, "An error occurred with the page: "+itemLink+" ; skip item. Error: "+ex.getMessage());
				//if an error occurred skip the item
				return returnItems;
			}
			
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
							/*
							else if(owlItem.getAttribute("class").equals("owl-item"))
							{
								if(logger!=null)
		            				logger.log(Level.FINE, "Look for html element img");
								DomNodeList<HtmlElement> imgs = owlItem.getElementsByTagName("img");
								if(imgs.size()>0)
								{
									if(pictureAlt1.equals(""))
										pictureAlt1 = imgs.get(0).getAttribute("src");
									else if (pictureAlt2.equals(""))
										pictureAlt2 = imgs.get(0).getAttribute("src");
									else if (pictureAlt3.equals(""))
										pictureAlt3 = imgs.get(0).getAttribute("src");
								}//if
							}//else if
							*/
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
    			
    			//clean images link
    			/*
    			if(picture.indexOf("?")!=-1)
    				picture = picture.substring(0, picture.indexOf("?"));
    			if(pictureAlt1.indexOf("?")!=-1)
    				pictureAlt1 = pictureAlt1.substring(0, pictureAlt1.indexOf("?"));
    			if(pictureAlt2.indexOf("?")!=-1)
    				pictureAlt2 = pictureAlt2.substring(0, pictureAlt2.indexOf("?"));	
    			if(pictureAlt3.indexOf("?")!=-1)
    				pictureAlt3 = pictureAlt3.substring(0, pictureAlt3.indexOf("?"));
    			if(pictureAlt4.indexOf("?")!=-1)
    				pictureAlt4 = pictureAlt4.substring(0, pictureAlt4.indexOf("?"));
    			*/
    			antonioliItem = new AntonioliItem();
    			antonioliItem.setItemID("ANTONIOLI-"+gender+"-"+season+"-"+style+color);
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
	        
	        final History window = webClient.getWebWindows().get(0).getHistory();
	        final Field f = window.getClass().getDeclaredField("ignoreNewPages_"); //NoSuchFieldException
	        f.setAccessible(true);
	        ((ThreadLocal<Boolean>) f.get(window)).set(Boolean.TRUE);
	        
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
    
    public List<String> getItemsLinkList()
    {
    	return this.itemsLinks;
    }   
    
    public String getWebSiteLink()
    {
    	return this.webSiteLink;
    }
    
    public List<AntonioliItem> getAllAntonioliItems()
    {
    	return this.antonioliItems;
    }
    
    public void setWebSiteLink(String webSiteLink)
    {
    	this.webSiteLink = webSiteLink;
    }
}