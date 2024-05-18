package com.example.pokedex;

public class Item {
    private String name;
    private String category;
    private String imageUrl;
    private int price;
    private String description;
    private String shortdescription;
    private String descriptionMotivation;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item(String name, String category, int price, String imageUrl, String description, String shortdescription, String descriptionMotivation) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description=description;
        this.shortdescription=shortdescription;
        this.descriptionMotivation=descriptionMotivation;
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



    // Getter y Setter para el nombre
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter y Setter para la categoría
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter y Setter para la URL de la imagen
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
