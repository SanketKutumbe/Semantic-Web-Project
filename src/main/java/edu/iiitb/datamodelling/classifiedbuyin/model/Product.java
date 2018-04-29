package edu.iiitb.datamodelling.classifiedbuyin.model;

public class Product {

    private int id;
    private String name;
    private String color;
    private String price;
    private String condensorType;
    private String channelType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String playerType;
    private String source;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getCondensorType() {
        return condensorType;
    }

    public void setCondensorType(String condensorType) {
        this.condensorType = condensorType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
