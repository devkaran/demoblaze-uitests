package com.demoblaze.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
public class CartPage {
    private final Page page;

    public boolean isProductsInCart(List<String> productsName) {
        boolean found = true;
        try {
            for(String productName: productsName) {
                page.waitForSelector("text=" + productName);
                found &= page.isVisible("text=" + productName);
            }
            return found;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    public Double getCartTotal(){
        page.waitForSelector("//h3[@id='totalp']");
        return Double.valueOf(page.innerText("//h3[@id='totalp']").replaceAll("\\D", ""));
    }

    public Double removeProductFromCartAndGetAmount(String productName) {
        String productLocator = String.format("(//td[text()='%s'])[last()]", productName);
        page.waitForSelector(productLocator);

        Double amount = Double.valueOf(page.locator(productLocator+"/..//td[3]").innerText().replaceAll("\\D", ""));
        page.locator(productLocator+"/..//a[text()='Delete']").click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {throw new RuntimeException(e);}
        page.waitForLoadState();
        return amount;
    }

    public void fillOrderFormAndPlaceOrder(OrderDetails orderDetails) {
        page.waitForSelector("//button[text()='Place Order']");
        page.click("//button[text()='Place Order']");
        page.waitForSelector("#name");
        page.fill("#name", orderDetails.getName());
        page.fill("#country", orderDetails.getCountry());
        page.fill("#city", orderDetails.getCity());
        page.fill("#card", orderDetails.getCreditCard());
        page.fill("#month", orderDetails.getMonth());
        page.fill("#year", orderDetails.getYear());
        page.click("text=Purchase");
        page.waitForLoadState();
    }

    public String getOrderConfirmationMessage(){
        page.waitForSelector("//div[@class='sweet-alert  showSweetAlert visible']");
        String dialogMessage = page.innerText(".sweet-alert h2");
        Objects.equals(dialogMessage, "Thank you for your purchase!");
        return page.innerText(".sweet-alert p");
    }

}
