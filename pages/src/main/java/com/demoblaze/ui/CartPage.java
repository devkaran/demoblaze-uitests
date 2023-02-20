package com.demoblaze.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Objects;

@Log4j2
@Builder
public class CartPage {
    private final Page page;

    public boolean isProductsInCart(Map<String, Double> productPriceMap) {
        boolean found = true;
        try {
            for(Map.Entry<String, Double> productEntry: productPriceMap.entrySet()) {
                page.waitForSelector("text=" + productEntry.getKey());
                found &= page.isVisible("text=" + productEntry.getKey());
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
        log.info("Removing product: {}", productName);

        Double amount = Double.valueOf(page.locator(productLocator+"/..//td[3]").innerText().replaceAll("\\D", ""));
        page.locator(productLocator+"/..//a[text()='Delete']").click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {throw new RuntimeException(e);}
        page.waitForLoadState();
        log.info("Deleted {} from cart", productName);
        return amount;
    }

    public void fillOrderFormAndPlaceOrder(OrderDetails orderDetails) {
        page.waitForSelector("//button[text()='Place Order']");
        log.info("Clicking purchase button");
        page.click("//button[text()='Place Order']");
        log.info("Filling order form details");
        page.waitForSelector("#name");
        page.fill("#name", orderDetails.getName());
        page.fill("#country", orderDetails.getCountry());
        page.fill("#city", orderDetails.getCity());
        page.fill("#card", orderDetails.getCreditCard());
        page.fill("#month", orderDetails.getMonth());
        page.fill("#year", orderDetails.getYear());
        log.info("Placing order");
        page.click("text=Purchase");
        page.waitForLoadState();
    }

    public String getOrderConfirmationMessage(){
        page.waitForSelector("//div[@class='sweet-alert  showSweetAlert visible']");
        String dialogMessage = page.innerText(".sweet-alert h2");
        Objects.equals(dialogMessage, "Thank you for your purchase!");
        log.info("Order placed successfully");
        return page.innerText(".sweet-alert p");
    }

}
