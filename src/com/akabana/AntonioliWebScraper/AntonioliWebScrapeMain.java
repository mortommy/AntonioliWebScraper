package com.akabana.AntonioliWebScraper;


//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntonioliWebScrapeMain {

	public static void main(String[] args)
	{
		String log_file_path = "c:\\";
	    String log_file_name = "antonioliwebscraper.log";
	    int log_file_max_size_MB = 5;
	    String log_level = "WARNING";
	    //String csv_file = "c:\\antonioli_items_download";
	    //boolean csv_append_timestap = false;
	    //String csv_separator = ";";
	    
	    String sqlite_db_file = "c:\\antonioli_items_download.db";
	    String mainUrl;
	    String subUrls;
	    
	    AntonioliWebScraperProperties awsp = null;
	    	
	    //configuration setting load
		try
		{
			awsp = new AntonioliWebScraperProperties();		
			awsp.loadProperties();
			log_file_path = awsp.getLogFilePath();
	    	log_file_name = awsp.getLogFileName();
	    	log_file_max_size_MB = Integer.parseInt(awsp.getLogFileMaxSizeMB());
	    	log_level = awsp.getLogFileLevel();
		}
		catch (Exception e1)
		{
			System.err.println("Error during the properties loading. "+e1.getMessage());
	    	System.exit(1);
		}	
		
		//log file creation
	    Logger logger = null;
	    Handler fileHandler = null;
	    try
	    {			
	    	logger = Logger.getLogger(AntonioliWebScrapeMain.class.getName());
			logger.setLevel(Level.parse(log_level));
		    fileHandler = new FileHandler(log_file_path+log_file_name,log_file_max_size_MB*1024*1024,5);
		    fileHandler.setFormatter(new LoggerFormatter());		    
		    logger.addHandler(fileHandler);
	    }
	    catch (Exception e2) 
		{
	    	System.err.println("Error during the file log creation. "+e2.getMessage());
	    	System.exit(1);
		}
	    
	    //start web scraping
	    try
	    {
	    	logger.log(Level.INFO, "Start the Web Scraping");
			mainUrl = awsp.getMainSiteURL();			
			subUrls = awsp.getCategoriesSiteURLs();
			String[] subURLs = subUrls.split(";");
			AntonioliItemWebDumper ad = null;
			AntonioliWebSiteScraper as = null;
			//the list that will containing all the items scrpaed objects
			List<AntonioliItem> items = new ArrayList<AntonioliItem>();
			//the list that will contains the links of products webpages skiped due to an error
			List<String> skippedProductWebPages = new ArrayList<String>();
			logger.log(Level.FINE, "Create the object AntonioliItemWebDumper");
			ad = new AntonioliItemWebDumper();
			logger.log(Level.INFO, "Get all historical downloaded items.");
			sqlite_db_file = awsp.getSqliteDB();
			List<AntonioliItem> historicalItems = (List<AntonioliItem>)ad.dumpSQLiteToList(sqlite_db_file);
			
			//from the list of url typed in the conf file we get the antonioli items objects
			for(int i=0; i<subURLs.length; i++)
			{
				logger.log(Level.FINE, "Create the object AntonioliWebSiteScraper");
				if(awsp.getUseProxy().equals("yes"))
					as = new AntonioliWebSiteScraper(mainUrl, awsp.getProxyHost(), awsp.getProxyPort(), awsp.getProxyUser(), awsp.getProxyPassword(), logger);
				else
					as = new AntonioliWebSiteScraper(mainUrl, logger);
				
				String[] subUrlAnMode = subURLs[i].split(",");
				if(subUrlAnMode.length != 2)
				{
					logger.log(Level.SEVERE, "Category wrong url parameter, mode or utl missing!");
					System.err.println("Category wrong url parameter, mode or url missing!");
			    	System.exit(1);
				}
				
				logger.log(Level.INFO, "scrap the category page: "+subUrlAnMode[0]);				
				
				List<AntonioliItem> newItems;
				if(subUrlAnMode[1].equals("U"))
				{
					logger.log(Level.INFO, "The category page is in update mode all the items pages needed to be scraped.");
					newItems = as.scrapeCategoryWebPage(subUrlAnMode[0], true);
				}
				else
				{
					logger.log(Level.INFO, "The category page is in insert mode only, scrape only new items pages using historical data as trace of new ones.");
					newItems = as.scrapeCategoryWebPageSmart(subUrlAnMode[0], true, historicalItems);
				}
				//add the AntonioliItems generated to the final list
				if(newItems != null)
					items.addAll(newItems);
				//add the list of skipped product webpages to the final list
				if(as.getSkippedItemsLinks() != null)
					skippedProductWebPages.addAll(as.getSkippedItemsLinks());
			}
			logger.log(Level.INFO, "Found "+Integer.toString(items.size())+" items.");	
			logger.log(Level.INFO, "Product web pages skipped due to errors: "+Integer.toString(skippedProductWebPages.size()));
			
			//download to csv
			/*
			csv_file = awsp.getCsvFilePathName();
			csv_separator = awsp.getCsvSeparator();
			csv_append_timestap = awsp.getCsvAddTimestamp();
			if(csv_append_timestap)
			{
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				csv_file = csv_file + sdf.format(timestamp); 
			}
			csv_file = csv_file + ".csv";
			logger.log(Level.INFO, "Download items to csv");
			*/
			
			//ad.dumpToCSVFile(items, csv_file, csv_separator);
			//logger.log(Level.INFO, "Created csv file: "+csv_file);
			
			//if the list has items, then items are download to a DB
			if(items.size()>0)
			{	
				logger.log(Level.INFO, "Download items to db");
				ad.dumpToSQLite(items, sqlite_db_file);
				logger.log(Level.INFO, "Saved items to db: "+sqlite_db_file);
			}
			//if there are skipped pages add them to the error links
			if(skippedProductWebPages.size()>0)
			{
				logger.log(Level.INFO, "Download skipped pages links to db");
				ad.dumpErrorsToSQLite(skippedProductWebPages, sqlite_db_file);
				logger.log(Level.INFO, "Saved skipped pages links to db: "+sqlite_db_file);
			}
			logger.log(Level.INFO, "End the Web Scraping");
		}
		catch (Exception e3)
		{
			logger.log(Level.SEVERE, "An error occourred: "+ e3.getMessage() );
        	System.exit(1);
		}
	}
}