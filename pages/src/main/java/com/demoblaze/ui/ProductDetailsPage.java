package com.demoblaze.ui;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;


@Builder
@Log4j2
public class ProductDetailsPage {
    @NonNull private final Page page;

    public void clickHome() {
        page.waitForSelector("text=Home");
        log.info("Clicking on the home icon.");
        page.click("text=Home");
    }

    public void clickCart() {
        page.waitForSelector("text=Cart");
        log.info("Clicking on the cart icon.");
        page.click("text=Cart");
    }

    public String getProductTitle() {
        page.waitForLoadState();
        ElementHandle title = page.waitForSelector("//h2[@class='name']");
        return title.innerText();
    }

    public Double getProductPrice() {
        ElementHandle price = page.querySelector("//h3[@class='price-container']");
        return Double.parseDouble(price.innerText().replaceAll("[^0-9]", ""));
    }

    public Double addProductToCard(ProductDetailsPage productDetailsPage){
        log.info("Product Name:: {}", productDetailsPage.getProductTitle());
        Double productPrice = productDetailsPage.getProductPrice();
        productDetailsPage.clickAddToCart();
        return productPrice;
    }

    public void clickAddToCart() {
        log.info("Adding product to cart");
        page.waitForLoadState();
        page.waitForSelector(".btn.btn-success.btn-lg");
        page.click(".btn.btn-success.btn-lg");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        page.waitForLoadState();
    }

    public void addAlertHandler(){
        page.onceDialog(dialog -> {
            if (dialog.message().contains("Product")) {
                log.info("Product added to cart successfully");
                dialog.accept();
                System.out.println("accepted");
            } else {
                log.warn("Unexpected alert text: {}", dialog.message());
                dialog.dismiss();
            }
        });
    }


}
