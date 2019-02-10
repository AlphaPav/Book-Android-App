package com.example.alphapav.book.util;

public class BookInfo {


    public static String[] ValidBookTypes={"人文类","传媒类","外语类", "经济类", "管理类", "理学类",
            "生命环境化学与地学类", "能源化工与高分子类", "'机械与材料类","建筑与土木类", "电气与自动化类", "航空航天与过程装备类","计算机类",
            "信息类", "海洋类", "农学类", "生工食品类","医学类", "药学类", "艺术设计类", "其他类"};
    private String BookID;
    private String Username;
    private String ISBN;
    private String CurrentImg;
    private String BookType;
    private String SellPrice;
    private String  Description;

    private String Title;
    private String Author;
    private String OriPrice;
    private String Publisher;

    private String Summary;

    private String OriImg;

    private String Catalog;


    public BookInfo(String _Username,String _ISBN, String _CurrentImgPath,String _BookType, String _SellPrice, String _Description) {
        Username= _Username;
        ISBN= _ISBN;
        CurrentImg=_CurrentImgPath;
        BookType=_BookType;
        SellPrice= _SellPrice;
        Description=_Description;
        if(_Description.length()>10000)
        {
            System.out.println("Description.length()>10000");
            Description=_Description.substring(0,9999);
            System.out.println(Description);
        }
    }

    public String getDescription() {
        return this.Description;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return this.OriImg;
    }

    /**
     * @param imagePath
     *            the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.OriImg = imagePath;
    }



    /**
     * @return the title
     */
    public String getTitle() {
        return this.Title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.Title = title;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return this.Author;
    }

    /**
     * @param author
     *            the author to set
     */
    public void setAuthor(String author) {
        this.Author = author;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return this.OriPrice;
    }

    public void setPrice(String price) {
        this.OriPrice = price;
    }


    public String getPublisher() {
        return this.Publisher;
    }

    public void setPublisher(String publisher) {
        this.Publisher = publisher;
    }

    public String getSummary() {
        return this.Summary;
    }

    public void setSummary(String summary) {
        this.Summary = summary;
    }


    public String getCatalog() {
        return this.Catalog;


    }

    public void setCatalog(String catalog) {
        this.Catalog = catalog;
    }




    public String getBookID() {
        return this.BookID;
    }
    public void setBookID(String BookID) {
        this.BookID =BookID;
    }

    public String getUsername() {
        return this.Username;
    }
    public void setUsername(String Username) {
        this.Username =Username;
    }

    public String getISBN() {
        return this.ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN =ISBN;
    }


    public String getCurrentImg() {
        return this.CurrentImg;
    }
    public void setCurrentImg(String CurrentImg) {
        this.CurrentImg =CurrentImg;
    }

    public String getBookType() {
        return this.BookType;
    }
    public void setBookType(String BookType) {
        this.BookType =BookType;
    }


    public String getSellPrice() {
        return this.SellPrice;
    }
    public void setSellPrice(String SellPrice) {
        this.SellPrice =SellPrice;
    }
}

