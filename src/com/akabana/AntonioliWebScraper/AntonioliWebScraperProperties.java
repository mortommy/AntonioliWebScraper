package com.akabana.AntonioliWebScraper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AntonioliWebScraperProperties {
	
	protected String log_file_path = "C:\\";
	protected String log_file_name="antonioliwebscraper.log";
	protected String log_file_max_size_MB = "5";
	protected String log_level = "INFO";
	protected String main_site_url = "https://www.antonioli.eu";
	protected String use_proxy = "";
	protected String proxy_host = "";
	protected String proxy_port = "";
	protected String proxy_user = "";
	protected String proxy_password = "";
	protected String categories_site_urls = "/en/CH/men/t/categories/clothing/jeans";
	
	protected String csv_file_path_name = "";
	protected String csv_add_timestamp = "";
	protected String csv_separator = ";";
	
	protected String sqlite_db_file = "c:\\antonioli_items_download.db";
	
	protected Properties prop;
	protected FileInputStream inputStream;
	protected String propFileName = "resources/config.properties";
	
	public AntonioliWebScraperProperties() throws Exception
	{
		try
		{
			prop = new Properties();	
			inputStream = new FileInputStream(propFileName);
			
		}
		catch (Exception e) 
		{
			throw new Exception(e);
		}
	}
	
	public void loadProperties() throws IOException
	{
		if (inputStream != null) 
		{
			prop.load(inputStream);
		} 
		else 
		{
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		this.log_file_path = prop.getProperty("log_file_path");
		this.log_file_name = prop.getProperty("log_file_name");
		this.log_file_max_size_MB = prop.getProperty("log_file_max_size_MB");
		this.log_level = prop.getProperty("log_level");
		this.main_site_url = prop.getProperty("main_site_url");
		this.use_proxy = prop.getProperty("use_proxy");
		this.proxy_host = prop.getProperty("proxy_host");
		this.proxy_port = prop.getProperty("proxy_port");
		this.proxy_user = prop.getProperty("proxy_user");
		this.proxy_password = prop.getProperty("proxy_password");
		this.categories_site_urls = prop.getProperty("categories_site_urls");
		this.csv_file_path_name = prop.getProperty("csv_file_path_name");
		this.csv_add_timestamp = prop.getProperty("csv_add_timestamp");
		this.csv_separator = prop.getProperty("csv_separator");
		this.sqlite_db_file = prop.getProperty("sqlite_db_file");
	}
	
	public String getLogFilePath()
	{
		return this.log_file_path;
	}
	
	public String getLogFileName()
	{
		return this.log_file_name;
	}
	
	public String getLogFileMaxSizeMB()
	{
		return this.log_file_max_size_MB;
	}
	
	public String getLogFileLevel()
	{
		return this.log_level;
	}
	
	public String getMainSiteURL()
	{
		return this.main_site_url;
	}

	public String getUseProxy()
	{
		return this.use_proxy;
	}
	
	public String getProxyHost()
	{
		return this.proxy_host;
	}
	
	public String getProxyPort()
	{
		return this.proxy_port;
	}
	
	public String getProxyUser()
	{
		return this.proxy_user;
	}
	
	public String getProxyPassword()
	{
		return this.proxy_password;
	}
	
	public String getCategoriesSiteURLs()
	{
		return this.categories_site_urls;
	}
	
	public String getCsvFilePathName()
	{
		return this.csv_file_path_name;
	}
	
	public boolean getCsvAddTimestamp()
	{
		if(this.csv_add_timestamp.equals("true"))
			return true;
		else
			return false;
	}
	
	public String getCsvSeparator()
	{
		return this.csv_separator;
	}
	
	public String getSqliteDB()
	{
		return this.sqlite_db_file;
	}	
}
