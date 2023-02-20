package com.demoblaze.ui;

import com.microsoft.playwright.Locator;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This utility class provides methods for performing various test operations,
 * such as adding products to the cart and validating order details.
 */
@Log4j2
@UtilityClass
public class TestUtils {

    /**
     * Adds the specified products to the cart and returns the total amount of the cart.
     *
     * @param productsLen total number of products to be added from homepage
     * @param homePage     The home page object.
     * @param productDetailsPage The product details page object.
     * @return The total amount of the cart after adding the products.
     */
    public Map<String, Double> addProductsToCartAndReturnTotalAmount(int productsLen, HomePage homePage, ProductDetailsPage productDetailsPage){
        Map<String, Double> productPriceMap = new LinkedHashMap<>();
        List<Locator> allProductsListedOnHomePage = homePage.getAllProductsListedOnHomePage();
        Assert.assertTrue(allProductsListedOnHomePage.size()>=productsLen, "Requested len products not listed in homepage, reduce the limit");
        // Loop through each product name and add it to the cart
        for (int j = 0; j < productsLen; j++) {
            String productName = allProductsListedOnHomePage.get(j).innerText();

            // Click on the product to go to its details page and add it to the cart
            homePage.clickProduct(productName);
            Double productPrice = productDetailsPage.addProductToCard(productDetailsPage);
            productDetailsPage.clickHome();
            productPriceMap.put(productName, productPrice);
        }
        return productPriceMap;
    }

    /**
     * Validates the order details based on the expected order details.
     *
     * @param orderStrings The order details string obtained from the cart page.
     * @param orderDetails The expected order details object.
     */
    public void validateOrderDetails(String orderStrings , OrderDetails orderDetails){
        String[] lines = orderStrings.split("\n");
        Assert.assertEquals(lines.length, 5);
        log.info("Validating placed order details");
        String id = lines[0].substring(lines[0].indexOf(":") + 1).trim();
        Double amount = Double.valueOf(lines[1].substring(lines[1].indexOf(":") + 1, lines[1].indexOf("USD")).trim());
        String cardNumber = lines[2].substring(lines[2].indexOf(":") + 1).trim();
        String name = lines[3].substring(lines[3].indexOf(":") + 1).trim();
        String date = lines[4].substring(lines[4].indexOf(":") + 1).trim();

        // Validate each order detail field against the expected values
        Assert.assertNotNull(id);
        Assert.assertEquals(amount.compareTo(orderDetails.getExpectedTotalAmount()), 0);
        Assert.assertEquals(cardNumber, orderDetails.getCreditCard());
        Assert.assertEquals(name, orderDetails.getName());
        Assert.assertEquals(date, getCurrentTimeStampLocalDate("dd/M/yyyy"));
    }

    /**
     * Returns the current timestamp formatted as a string in the specified date pattern.
     *
     * @param datePattern The date pattern to format the timestamp with.
     * @return The current timestamp formatted as a string.
     */
    public String getCurrentTimeStampLocalDate(String datePattern) {
        return LocalDateTime.now().minusMonths(1) // handling java script getMonth() return value starts from 0th
                .format(DateTimeFormatter.ofPattern(datePattern));
    }
}
