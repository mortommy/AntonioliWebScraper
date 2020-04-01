package com.akabana.AntonioliWebScraper;

import com.akabana.ItemWebScraper.*;

public class AntonioliItem extends ItemWebScraper {
	
	protected String itemAvailability;
	
	
	public AntonioliItem()
	{
		this.itemSource = "antonioli";
		this.itemAvailability = "";
		this.itemID = "";
		this.itemSku = "";
		this.itemName = "";
		this.itemBrand = "";
		this.itemModel = "";
		this.itemPart = "";
		this.itemColor = "";
		this.itemSize = "";
		this.itemDim = "";
		this.itemDescription = "";
		this.itemGender = "";
		this.itemCategory = "";
		this.itemHierarchy1 = "";
		this.itemHierarchy2 = "";
		this.itemHierarchy3 = "";
		this.itemHierarchy4 = "";
		this.itemSeason = "";
		this.itemPrice = "";
		this.itemPriceCurrency = "";
		this.itemLink = "";
		this.itemPicture = "";
	}
	
	public void setItemAvailability(String itemAvailability)
	{		
		this.itemAvailability = itemAvailability;
	}
	
	public String getItemAvailability()
	{
		return this.itemAvailability;
	}
}