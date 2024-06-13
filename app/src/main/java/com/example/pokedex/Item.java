package com.example.pokedex;

public class Item {
    private String name;
    private String category;
    private String imageUrl;
    private int price;
    private String description;
    private String shortdescription;
    private String descriptionMotivation;
    private int quantity;

    public Item(String name, String category, int price, String imageUrl, String description, String shortdescription, String descriptionMotivation, int quantity) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
        this.shortdescription = shortdescription;
        this.descriptionMotivation = descriptionMotivation;
        this.quantity = quantity;

    }

    public Item(String name) {
        this.name = name;
    }
    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortdescription() {
        return shortdescription;
    }

    public void setShortdescription(String shortdescription) {
        this.shortdescription = shortdescription;
    }

    public String getDescriptionMotivation() {
        return descriptionMotivation;
    }

    public void setDescriptionMotivation(String descriptionMotivation) {
        this.descriptionMotivation = descriptionMotivation;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
