package com.kalhara.eshopfinal.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String productId;
    private String title;
    private String description;
    private Double price;
    private String categoryId;
    private List<String> images;
    private int stockCount;
    private boolean status;
    private float rating;
}
