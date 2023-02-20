package com.demoblaze.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

@Log4j2
public class EndToEndTest {
    /**
     This method tests the adding and removing of products to/from the cart
     @param testConfiguration test configuration for the test
     @param browser the browser in which to run the test
     @param orderDetailsFormInput order details input form for order placement
     */
    @Test(dataProvider = "testDP", dataProviderClass = TestDataProvider.class)
    public void addToCartTest(int totalItemsToAdd, TestConfiguration testConfiguration, Browser browser, OrderDetails orderDetailsFormInput) {
// create a new browser context and page
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

// navigate to home page and initialize page objects
        HomePage homePage = HomePage.builder().page(page).pageUrl(testConfiguration.getBaseUrl()).build();
        homePage.navigateTo();
        ProductDetailsPage productDetailsPage = ProductDetailsPage.builder().page(homePage.getPage()).build();

// add products to cart and get the total amount
        productDetailsPage.addAlertHandler();
        Map<String, Double> productPriceMap = TestUtils.addProductsToCartAndReturnTotalAmount(totalItemsToAdd, homePage, productDetailsPage);

// navigate to cart and validate products in cart and total amount
        productDetailsPage.clickCart();
        CartPage cartPage = CartPage.builder().page(page).build();
        Assert.assertTrue(cartPage.isProductsInCart(productPriceMap));
        double totalAmount = productPriceMap.values().stream().mapToDouble(Double::doubleValue).sum();
        Assert.assertEquals(cartPage.getCartTotal().compareTo(totalAmount), 0);

// remove last product from cart and validate the new total amount
        String productToBeRemoved = productPriceMap.keySet().stream().skip(productPriceMap.size() - 1).findFirst().orElse(null);
        Double removedProductAmount = cartPage.removeProductFromCartAndGetAmount(productToBeRemoved);
        Assert.assertEquals(removedProductAmount, productPriceMap.get(productToBeRemoved));
        totalAmount -= removedProductAmount;
        Assert.assertEquals(cartPage.getCartTotal().compareTo(totalAmount), 0);
        productPriceMap.remove(productToBeRemoved);

// validate the remaining products in cart and place the order
        Assert.assertTrue(cartPage.isProductsInCart(productPriceMap));
        orderDetailsFormInput = orderDetailsFormInput.toBuilder().expectedTotalAmount(totalAmount).build();
        cartPage.fillOrderFormAndPlaceOrder(orderDetailsFormInput);
        TestUtils.validateOrderDetails(cartPage.getOrderConfirmationMessage(), orderDetailsFormInput);

// close the browser context
        context.close();
    }
}
