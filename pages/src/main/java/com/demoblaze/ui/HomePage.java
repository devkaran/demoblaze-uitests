package com.demoblaze.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Builder
@Log4j2
public class HomePage {
    @Getter private final Page page;
    private final String pageUrl;

    public void navigateTo() {
        page.navigate(pageUrl);
        page.waitForLoadState();
    }

    public void clickProduct(String productName) {
        page.click("text=" + productName);
        page.waitForLoadState();
    }

    public boolean isProductDisplayed(String productName){
        String productSelector = String.format("//a[contains(text(),'%s')]", productName);
        log.info("Checking if product is displayed: {}", productName);
        page.waitForSelector(productSelector);
        return page.isVisible(productSelector);
    }

    public List<Locator> getAllProductsListedOnHomePage(){
        page.waitForSelector("//div[@id='tbodyid']//a[@class='hrefch']");
        return page.locator("//div[@id='tbodyid']//a[@class='hrefch']").all();
    }
}
