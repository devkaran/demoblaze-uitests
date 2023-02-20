package com.demoblaze.ui;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class OrderDetails {
    String name;
    String country;
    String city;
    String creditCard;
    String month;
    String year;
    Double expectedTotalAmount;

}

