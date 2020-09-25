package com.chong.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    private String id;
    private String itemName;
    private Category category;
    private Long cost;
    private Long stock;

    public static enum Category{
        FOOD, DEVICE, CLOTHES
    }
}
