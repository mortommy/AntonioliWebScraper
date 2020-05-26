package com.akabana.AntonioliWebScraper;

import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.akabana.Item.Item;
import com.akabana.ItemWebDumper.ItemsWebDumper;
import com.akabana.db_utils.*;

public class AntonioliItemWebDumper implements ItemsWebDumper {

	/***
	 * Exports ItemWebScraper implemented objects to a CSV file
	 * @param items list of objects that implement ItemWebScraper
	 * @param filePathName path and name of destination file
	 * @param separator character used as data separator 
	 */
	public void dumpToCSVFile(List<? extends Item> items, String filePathName, String separator) 
	{
		try
		{
			FileWriter csvWriter = new FileWriter(filePathName);
			//csv file head
			csvWriter.append("itemID");
			csvWriter.append(separator);
			csvWriter.append("itemSKU");
			csvWriter.append(separator);
			csvWriter.append("itemName");
			csvWriter.append(separator);
			csvWriter.append("itemBrand");
			csvWriter.append(separator);
			csvWriter.append("itemModel");
			csvWriter.append(separator);
			csvWriter.append("itemPart");
			csvWriter.append(separator);
			csvWriter.append("itemStyle");
			csvWriter.append(separator);
			csvWriter.append("itemColor");
			csvWriter.append(separator);
			csvWriter.append("itemSize");
			csvWriter.append(separator);
			csvWriter.append("itemDim");
			csvWriter.append(separator);
			csvWriter.append("itemDescription");
			csvWriter.append(separator);
			csvWriter.append("itemGender");
			csvWriter.append(separator);
			csvWriter.append("itemCategory");
			csvWriter.append(separator);
			csvWriter.append("itemHierarchy1");
			csvWriter.append(separator);
			csvWriter.append("itemHierarchy2");
			csvWriter.append(separator);
			csvWriter.append("itemHierarchy3");
			csvWriter.append(separator);
			csvWriter.append("itemHierarchy4");
			csvWriter.append(separator);
			csvWriter.append("itemSeason");
			csvWriter.append(separator);
			csvWriter.append("itemPrice");
			csvWriter.append(separator);
			csvWriter.append("itemPriceCurrency");
			csvWriter.append(separator);
			csvWriter.append("itemLink");
			csvWriter.append(separator);
			csvWriter.append("itemPicture");
			csvWriter.append(separator);
			csvWriter.append("itemPictureAlt1");
			csvWriter.append(separator);
			csvWriter.append("itemPictureAl2");
			csvWriter.append(separator);
			csvWriter.append("itemPictureAlt3");
			csvWriter.append(separator);
			csvWriter.append("itemPictureAlt4");
			csvWriter.append(separator);
			csvWriter.append("itemAvailabiltiy");
			csvWriter.append(separator);
			csvWriter.append("itemSource");
			csvWriter.append("\n");
			
			//csv file data
			for(Item item : items)
			{
				AntonioliItem item2 = (AntonioliItem)item;
				csvWriter.append(item2.getItemID());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemSku());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemName());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemBrand());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemModel());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPart());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemStyle());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemColor());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemSize());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemDim());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemDescription());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemGender());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemCategory());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemHierarchy1());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemHierarchy2());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemHierarchy3());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemHierarchy4());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemSeason());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPrice());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPriceCurrency());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemLink());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPicture());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPictureAlt1());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPictureAlt2());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPictureAlt3());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemPictureAlt4());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemAvailability());
				csvWriter.append(separator);
				csvWriter.append(item2.getItemSource());
				csvWriter.append("\n");			
			}//for
			csvWriter.flush();
			csvWriter.close();
		} 
		catch (Exception e) 
		{			
			System.err.println("Error :"+e.getMessage());
	    	System.exit(1);
		}		
	}

	/***
	 * Exports ItemWebScraper implemented objects to a SQLite db
	 * @param items list of objects that implement ItemWebScraper
	 * @param filePathName path and name of destination db
	 */
	public void dumpToSQLite(List<? extends Item> items, String dbPathName) 
	{
		try
		{
			//connect to the DB
			DBMS_DB sqliteDB = new SQLLite_DB(dbPathName);
			sqliteDB.connectToDB();
			String sqlStatement = "";
			
			//check if the destination tabel exists
			if(!sqliteDB.tableExists("ANTONIOLI_DOWNLOADED_ITEMS"))
			{
				//create the destination table
				sqlStatement = "CREATE TABLE ANTONIOLI_DOWNLOADED_ITEMS " +
										"(ITEM_ID TEXT NOT NULL," +
										"ITEM_SKU TEXT NOT NULL,"+
										"ITEM_NAME TEXT NOT NULL,"+
										"ITEM_BRAND TEXT NOT NULL,"+
										"ITEM_MODEL TEXT,"+
										"ITEM_PART TEXT,"+
										"ITEM_STYLE TEXT,"+
										"ITEM_COLOR TEXT,"+
										"ITEM_SIZE TEXT,"+
										"ITEM_DIM TEXT,"+
										"ITEM_DESCRIPTION TEXT,"+
										"ITEM_GENDER TEXT NOT NULL,"+
										"ITEM_CATEGORY TEXT,"+
										"ITEM_HIERARCHY1 TEXT,"+
										"ITEM_HIERARCHY2 TEXT,"+
										"ITEM_HIERARCHY3 TEXT,"+
										"ITEM_HIERARCHY4 TEXT,"+
										"ITEM_SEASON TEXT,"+
										"ITEM_PRICE TEXT NOT NULL,"+
										"ITEM_PRICE_CURRENCY TEXT NOT NULL,"+
										"ITEM_LINK TEXT,"+
										"ITEM_PICTURE TEXT,"+
										"ITEM_PICTURE_ALT1 TEXT,"+
										"ITEM_PICTURE_ALT2 TEXT,"+
										"ITEM_PICTURE_ALT3 TEXT,"+
										"ITEM_PICTURE_ALT4 TEXT,"+
										"ITEM_AVALAIBILITY TEXT,"+
										"ITEM_SOURCE TEXT NOT NULL,"+
										"CREATED_TIMESTAMP DATETIME NOT NULL);";
				sqliteDB.executeUpdate(sqlStatement);
			}//if
			
			//check if the destination tabel exists
			if(!sqliteDB.tableExists("ANTONIOLI_ITEMS"))
			{
				//create the destination table
				sqlStatement = "CREATE TABLE ANTONIOLI_ITEMS " +
										"(ITEM_ID TEXT NOT NULL," +
										"ITEM_SKU TEXT NOT NULL,"+
										"ITEM_NAME TEXT NOT NULL,"+
										"ITEM_BRAND TEXT NOT NULL,"+
										"ITEM_MODEL TEXT,"+
										"ITEM_PART TEXT,"+
										"ITEM_STYLE TEXT,"+
										"ITEM_COLOR TEXT,"+
										"ITEM_SIZE TEXT,"+
										"ITEM_DIM TEXT,"+
										"ITEM_DESCRIPTION TEXT,"+
										"ITEM_GENDER TEXT NOT NULL,"+
										"ITEM_CATEGORY TEXT,"+
										"ITEM_HIERARCHY1 TEXT,"+
										"ITEM_HIERARCHY2 TEXT,"+
										"ITEM_HIERARCHY3 TEXT,"+
										"ITEM_HIERARCHY4 TEXT,"+
										"ITEM_SEASON TEXT,"+
										"ITEM_PRICE TEXT NOT NULL,"+
										"ITEM_PRICE_CURRENCY TEXT NOT NULL,"+
										"ITEM_LINK TEXT,"+
										"ITEM_PICTURE TEXT,"+
										"ITEM_PICTURE_ALT1 TEXT,"+
										"ITEM_PICTURE_ALT2 TEXT,"+
										"ITEM_PICTURE_ALT3 TEXT,"+
										"ITEM_PICTURE_ALT4 TEXT,"+
										"ITEM_AVALAIBILITY TEXT,"+
										"ITEM_SOURCE TEXT NOT NULL,"+
										"CREATED_TIMESTAMP DATETIME NOT NULL);";
				sqliteDB.executeUpdate(sqlStatement);
			}//if
			
			//delete existing data
			sqlStatement = "DELETE from ANTONIOLI_DOWNLOADED_ITEMS;";
			sqliteDB.executeUpdate(sqlStatement);
			//insert items
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");	
			for(Item item : items)
			{
				AntonioliItem item2 = (AntonioliItem)item;
				sqlStatement = "INSERT INTO ANTONIOLI_DOWNLOADED_ITEMS (ITEM_ID,ITEM_SKU,ITEM_NAME,ITEM_BRAND,ITEM_MODEL,ITEM_PART,ITEM_STYLE,ITEM_COLOR,ITEM_SIZE,ITEM_DIM,ITEM_DESCRIPTION,ITEM_GENDER,ITEM_CATEGORY,ITEM_HIERARCHY1,ITEM_HIERARCHY2,ITEM_HIERARCHY3,ITEM_HIERARCHY4,ITEM_SEASON,ITEM_PRICE,ITEM_PRICE_CURRENCY,ITEM_LINK,ITEM_PICTURE,ITEM_PICTURE_ALT1,ITEM_PICTURE_ALT2,ITEM_PICTURE_ALT3,ITEM_PICTURE_ALT4,ITEM_AVALAIBILITY,ITEM_SOURCE,CREATED_TIMESTAMP) " +
								"VALUES('"+item2.getItemID()+"','"+item2.getItemSku()+"','"+item2.getItemName()+"','"+item2.getItemBrand()+"','"+
								item2.getItemModel()+"','"+item2.getItemPart()+"','"+item2.getItemStyle()+"','"+item2.getItemColor()+"','"+item2.getItemSize()+"','"+
								item2.getItemDim()+"','"+item2.getItemDescription().replace("'"," ")+"','"+item2.getItemGender()+"','"+item2.getItemCategory()+"','"+
								item2.getItemHierarchy1()+"','"+item2.getItemHierarchy2()+"','"+item2.getItemHierarchy3()+"','"+item2.getItemHierarchy4()+"','"+
								item2.getItemSeason()+"','"+item2.getItemPrice()+"','"+item2.getItemPriceCurrency()+"','"+item2.getItemLink()+"','"+
								item2.getItemPicture()+"','"+item2.getItemPictureAlt1()+"','"+item2.getItemPictureAlt2()+"','"+item2.getItemPictureAlt3()+"','"+item2.getItemPictureAlt4()+"','"+
								item2.getItemAvailability()+"','"+item2.getItemSource()+"','"+sdf.format(timestamp)+"'"+
								");";
				sqliteDB.executeUpdate(sqlStatement);
							
			}//for		
			
			//keep history of all downloaded items
			sqlStatement = "INSERT INTO ANTONIOLI_ITEMS (ITEM_ID,ITEM_SKU,ITEM_NAME,ITEM_BRAND,ITEM_MODEL,ITEM_PART,ITEM_STYLE,ITEM_COLOR,ITEM_SIZE,ITEM_DIM,ITEM_DESCRIPTION,ITEM_GENDER,ITEM_CATEGORY,ITEM_HIERARCHY1,ITEM_HIERARCHY2,ITEM_HIERARCHY3,ITEM_HIERARCHY4,ITEM_SEASON,ITEM_PRICE,ITEM_PRICE_CURRENCY,ITEM_LINK,ITEM_PICTURE,ITEM_PICTURE_ALT1,ITEM_PICTURE_ALT2,ITEM_PICTURE_ALT3,ITEM_PICTURE_ALT4,ITEM_AVALAIBILITY,ITEM_SOURCE,CREATED_TIMESTAMP) " +
						"SELECT * "+
						"FROM ANTONIOLI_DOWNLOADED_ITEMS "+
						"WHERE (ITEM_SKU, ITEM_SIZE) NOT IN " + 
						"(SELECT ITEM_SKU, ITEM_SIZE FROM ANTONIOLI_ITEMS)";
			sqliteDB.executeUpdate(sqlStatement);
			
			sqliteDB.closeDBConnection();
		} 
		catch (Exception e) 
		{			
			System.err.println("Error: "+e.getMessage());
	    	System.exit(1);
		}
	}
	
	/***
	 * Used to manange errors, expors list of errors to a csv file
	 * @param items list of string to be esxported
	 * @param filePathName path and name of destination file
	 */
	public void dumpErrorsToCSVFile(List<String> errors, String filePathName) 
	{
		try
		{
			FileWriter csvWriter = new FileWriter(filePathName);
			for(String error : errors)
			{
				csvWriter.append(error);
				csvWriter.append("\n");	
			}
			csvWriter.flush();
			csvWriter.close();
		}
		catch (Exception e) 
		{			
			System.err.println("Error :"+e.getMessage());
	    	System.exit(1);
		}
		
	}

	/***
	 * Used to manange errors, expors list of errors to a SQLite table (ERROR_WEB_PAGES)
	 * @param items list of string to be esxported
	 * @param filePathName path and name of destination file
	 */
	public void dumpErrorsToSQLite(List<String> errors, String dbPathName) {
		try
		{
			DBMS_DB sqliteDB = new SQLLite_DB(dbPathName);
			sqliteDB.connectToDB();
			String sqlStatement = "";
			
			//check if the destination tabel exists
			if(!sqliteDB.tableExists("ERROR_WEB_PAGES"))
			{
				//create the destination table
				sqlStatement = "CREATE TABLE ERROR_WEB_PAGES ( "+
						"LINK TEXT;)";
			}
			//delete existing data
			sqlStatement = "DELETE from ERROR_WEB_PAGES;";
			sqliteDB.executeUpdate(sqlStatement);
			for(String error : errors)
			{
				sqlStatement = "INSERT INTO ERROR_WEB_PAGES (LINK) " +
								"VALUES('"+error+"');";
				sqliteDB.executeUpdate(sqlStatement);
			}//for	
			sqliteDB.closeDBConnection();
		}
		catch (Exception e) 
		{			
			System.err.println("Error: "+e.getMessage());
	    	System.exit(1);
		}		
	}
	
	/***
	 * from a table (ANTONIOLI_ITEMS) in a SQLite DB get a list of Antonioli Items
	 * @param filePathName path and name of source db
	 * @return List of Antonioli Items
	 */
	public List<? extends Item> dumpSQLiteToList(String dbPathName) {
		List<AntonioliItem> result = new ArrayList<AntonioliItem>();
		AntonioliItem item;
		try
		{
			
			DBMS_DB sqliteDB = new SQLLite_DB(dbPathName);
			sqliteDB.connectToDB();
			String sqlStatement = "";
			
			//check if the source tabel exists
			if(!sqliteDB.tableExists("ANTONIOLI_ITEMS"))
			{
				throw new Exception("No destination table ANTONIOLI_ITEMS found in "+dbPathName);
			}
			
			sqlStatement = "SELECT * FROM ANTONIOLI_ITEMS;";
			
			ResultSet sourceRs = sqliteDB.executeSelect(sqlStatement);
			
			while (sourceRs.next()) 
			{
				item = new AntonioliItem();
				item.setItemID(sourceRs.getString("ITEM_ID"));
				item.setItemSku(sourceRs.getString("ITEM_SKU"));
				item.setItemName(sourceRs.getString("ITEM_NAME"));
				item.setItemBrand(sourceRs.getString("ITEM_BRAND"));
				item.setItemModel(sourceRs.getString("ITEM_MODEL"));
				item.setItemPart(sourceRs.getString("ITEM_PART"));
				item.setItemStyle(sourceRs.getString("ITEM_STYLE"));
    			item.setItemColor(sourceRs.getString("ITEM_COLOR"));
    			item.setItemSize(sourceRs.getString("ITEM_SIZE"));
    			item.setItemDim(sourceRs.getString("ITEM_DIM"));
    			item.setItemDescription(sourceRs.getString("ITEM_DESCRIPTION"));
    			item.setItemGender(sourceRs.getString("ITEM_GENDER"));
    			item.setItemCategory(sourceRs.getString("ITEM_CATEGORY"));
    			item.setItemHierarchy1(sourceRs.getString("ITEM_HIERARCHY1"));
    			item.setItemHierarchy2(sourceRs.getString("ITEM_HIERARCHY2"));
    			item.setItemHierarchy3(sourceRs.getString("ITEM_HIERARCHY3"));
    			item.setItemHierarchy4(sourceRs.getString("ITEM_HIERARCHY4"));
    			item.setItemSeason(sourceRs.getString("ITEM_SEASON"));
    			item.setItemPrice(sourceRs.getString("ITEM_PRICE"));
    			item.setItemPriceCurrency(sourceRs.getString("ITEM_PRICE_CURRENCY"));
    			item.setItemLink(sourceRs.getString("ITEM_LINK"));
    			item.setItemPicture(sourceRs.getString("ITEM_PICTURE"));
    			item.setItemPictureAlt1(sourceRs.getString("ITEM_PICTURE_ALT1"));
    			item.setItemPictureAlt2(sourceRs.getString("ITEM_PICTURE_ALT2"));
    			item.setItemPictureAlt3(sourceRs.getString("ITEM_PICTURE_ALT3"));
    			item.setItemPictureAlt4(sourceRs.getString("ITEM_PICTURE_ALT4"));
    			item.setItemAvailability(sourceRs.getString("ITEM_AVALAIBILITY"));
    			item.setItemSource(sourceRs.getString("ITEM_SOURCE")); 			
    			result.add(item);
			}//while
			sqliteDB.closeDBConnection();
		}
		catch (Exception e) 
		{			
			System.err.println("Error: "+e.getMessage());
	    	System.exit(1);
		}
		return result;		
	}
}//class
