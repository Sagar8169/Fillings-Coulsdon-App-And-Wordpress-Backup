package com.example.stockmanager.network;

import java.util.List;

public class Product {
    private int id;
    private String name;
    private String type; // 👈 Product ka type (simple ya variable)
    private List<Image> images; // 👈 WooCommerce images list
    private List<Product> variations; // 👈 Variations ka list (sirf variable products ke liye)

    // ✅ Constructor
    public Product(int id, String name, String type, List<Image> images, List<Product> variations) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.images = images;
        this.variations = variations;
    }

    // ✅ Getter Methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<Product> getVariations() {
        return variations;
    }

    // ✅ Get first image URL
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getSrc();  // 👈 Pehli image ka URL return karega
        }
        return null; // Agar image na ho to null return karega
    }

    // ✅ Nested Class for Image
    public static class Image {
        private String src;

        public String getSrc() {
            return src;
        }
    }
}
