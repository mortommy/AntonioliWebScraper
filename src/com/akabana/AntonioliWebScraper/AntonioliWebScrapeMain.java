package com.akabana.AntonioliWebScraper;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
	    String csv_file = "c:\\antonioli_items_download";
	    boolean csv_append_timestap = false;
	    String csv_separator = ";";
	    
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
			String[] subURLs = subUrls.split(",");
			AntonioliItemWebDumper ad = null;
			AntonioliWebSiteScraper as = null;
			List<AntonioliItem> items = null;
			
			for(int i=0; i<subURLs.length; i++)
			{
				logger.log(Level.FINE, "Create the object AntonioliWebSiteScraper");
				as = new AntonioliWebSiteScraper(mainUrl, logger);
				logger.log(Level.INFO, "scrap the catecory page: "+subURLs[i]);
				items = as.scrapeCategoryWebPage(subURLs[i], true);							
			}
			logger.log(Level.INFO, "Found "+Integer.toString(items.size())+" items.");	
			
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
			ad = new AntonioliItemWebDumper();
			//ad.dumpToCSVFile(items, csv_file, csv_separator);
			//logger.log(Level.INFO, "Created csv file: "+csv_file);
			
			//save to db
			if(items.size()>0)
			{	
				sqlite_db_file = awsp.getSqliteDB();
				logger.log(Level.INFO, "Download items to db");
				ad.dumpToSQLite(items, sqlite_db_file);
				logger.log(Level.INFO, "Saved items to db: "+sqlite_db_file);
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