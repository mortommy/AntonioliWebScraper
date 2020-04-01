package com.akabana.AntonioliWebScraper;

import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.akabana.ItemWebDumper.ItemsWebDumper;
import com.akabana.ItemWebScraper.ItemWebScraper;
import com.akabana.db_utils.*;

public class AntonioliItemWebDumper implements ItemsWebDumper {

	/***
	 * Exports ItemWebScraper implemented objects to a CSV file
	 * @param items list of objects that implement ItemWebScraper
	 * @param filePathName path and name of destination file
	 * @param separator character used as data separator 
	 */
	public void dumpToCSVFile(List<? extends ItemWebScraper> items, String filePathName, String separator) 
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
			for(ItemWebScraper item : items)
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
	public void dumpToSQLite(List<? extends ItemWebScraper> items, String dbPathName) 
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
			//insert data
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");	
			//delete existing data
			sqlStatement = "DELETE from ANTONIOLI_DOWNLOADED_ITEMS;";
			sqliteDB.executeUpdate(sqlStatement);
			//insert items
			for(ItemWebScraper item : items)
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
			sqliteDB.closeDBConnection();
		} 
		catch (Exception e) 
		{			
			System.err.println("Error: "+e.getMessage());
	    	System.exit(1);
		}
	}
}//class
