package com.example.pokedex;

public class item {
    private String name;
    private String category;
    private String imageUrl;
    private int price;

    public item(String name, String category,int price, String imageUrl) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
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
