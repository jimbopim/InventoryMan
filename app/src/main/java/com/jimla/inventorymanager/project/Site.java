package com.jimla.inventorymanager.project;

public class Site {
    int siteId;
    int siteType;
    String siteName;
    String description;
    int startDate;

    public Site(int siteId, int siteType, String siteName, String description, int startDate) {
        this.siteId = siteId;
        this.siteType = siteType;
        this.siteName = siteName;
        this.description = description;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteId=" + siteId +
                ", siteType=" + siteType +
                ", siteName='" + siteName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
