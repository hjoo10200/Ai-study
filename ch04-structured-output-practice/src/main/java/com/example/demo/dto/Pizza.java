package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class Pizza {
    private String name;
    private PizzaSize size;
    private List<String> toppings;

    public enum PizzaSize {
        LARGE,
        MEDIUM,
        SMALL
    }
}
