package com.jaldeeinc.jaldee.model;

/**
 * Created by sharmila on 18/7/18.
 */

public class ListCell implements Comparable<ListCell>{

    private String name;
    private String category;

    public String getMdisplayname() {
        return mdisplayname;
    }

    public void setMdisplayname(String mdisplayname) {
        this.mdisplayname = mdisplayname;
    }

    private String mdisplayname;
    private boolean isSectionHeader;

    public String getMsector() {
        return msector;
    }

    public void setMsector(String msector) {
        this.msector = msector;
    }

    String msector;

    public ListCell(String name, String category,String sector,String displayname)
    {
        this.name = name;
        this.category = category;
        this.msector=sector;
        isSectionHeader = false;
        this.mdisplayname=displayname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public void setToSectionHeader()
    {
        isSectionHeader = true;
    }
    public void setToSectionHeaderNew(boolean isheader)
    {
        isSectionHeader = isheader;
    }
    public boolean isSectionHeader()
    {
        return isSectionHeader;
    }

    @Override
    public int compareTo(ListCell other) {
        return this.category.compareTo(other.category);
    }
}
