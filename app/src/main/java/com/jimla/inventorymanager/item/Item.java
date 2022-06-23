package com.jimla.inventorymanager.item;

public class Item {

    public int siteId;
    public int roomId;
    public int itemId;
    public int itemType;
    public String itemName;
    public String itemDescription;
    public String itemEpc;
    public String manufacturer;
    public int manufacturingYear;
    public int salesPrice;
    public int width;
    public int height;
    public int length;
    public int status;
    public int makeBuyCode;
    public int orderNumber;
    public int lineNumber;
    public int lineSuffix;
    public int quantity;
    public String itemNumber;
    public String configurationText;
    public String note;

    public Item(int itemId, String itemName, String itemDescription) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }
}
