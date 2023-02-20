package com.demoblaze.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Log4j2
public class EndToEndTest {
    /**
     This method tests the adding and removing of products to/from the cart
     @param testConfiguration test configuration for the test
     @param browser the browser in which to run the test
     @param sampleProductNames list of sample product names to be added to cart
     @param orderDetailsFormInput order details input form for order placement
     */
    @Test(dataProvider = "testDP", dataProviderClass = TestDataProvider.class)
    public void addToCartTest(TestConfiguration testConfiguration, Browser browser, List<String> sampleProductNames, OrderDetails orderDetailsFormInput) {
// create a new browser context and page
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

// navigate to home page and initialize page objects
        HomePage homePage = HomePage.builder().page(page).pageUrl(testConfiguration.getBaseUrl()).build();
        homePage.navigateTo();
        ProductDetailsPage productDetailsPage = ProductDetailsPage.builder().page(homePage.getPage()).build();

// add products to cart and get the total amount
        productDetailsPage.addAlertHandler();
        Double totalAmount = TestUtils.addProductsToCartAndReturnTotalAmount(sampleProductNames, homePage, productDetailsPage);

// navigate to cart and validate products in cart and total amount
        productDetailsPage.clickCart();
        CartPage cartPage = CartPage.builder().page(page).build();
        Assert.assertTrue(cartPage.isProductsInCart(sampleProductNames));
        Assert.assertEquals(cartPage.getCartTotal().compareTo(totalAmount), 0);

// remove last product from cart and validate the new total amount
        Double removedProductAmount = cartPage.removeProductFromCartAndGetAmount(sampleProductNames.get(sampleProductNames.size() - 1));
        totalAmount -= removedProductAmount;
        Assert.assertEquals(cartPage.getCartTotal().compareTo(totalAmount), 0);
        sampleProductNames.remove(sampleProductNames.size() - 1);

// validate the remaining products in cart and place the order
        Assert.assertTrue(cartPage.isProductsInCart(sampleProductNames));
        orderDetailsFormInput = orderDetailsFormInput.toBuilder().expectedTotalAmount(totalAmount).build();
        cartPage.fillOrderFormAndPlaceOrder(orderDetailsFormInput);
        TestUtils.validateOrderDetails(cartPage.getOrderConfirmationMessage(), orderDetailsFormInput);

// close the browser context
        context.close();
    }
}
