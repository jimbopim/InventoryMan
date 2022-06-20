package com.jimla.inventorymanager.item;

public class Item {

    int siteId;
    int roomId;
    int itemId;
    int itemType;
    String itemName;
    String itemDescription;
    String itemEpc;
    String manufacturer;
    int manufacturingYear;
    int salesPrice;
    int width;
    int height;
    int length;
    int status;
    int makeBuyCode;
    int orderNumber;
    int lineNumber;
    int lineSuffix;
    int quantity;
    String itemNumber;
    String configurationText;
    String note;

    public Item(int itemId, String itemName, String itemDescription) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }
}
