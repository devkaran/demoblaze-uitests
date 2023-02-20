package com.demoblaze.ui;

import com.microsoft.playwright.Locator;
import lombok.experimental.UtilityClass;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This utility class provides methods for performing various test operations,
 * such as adding products to the cart and validating order details.
 */
@UtilityClass
public class TestUtils {

    /**
     * Adds the specified products to the cart and returns the total amount of the cart.
     *
     * @param productNames List of product names to add to the cart.
     * @param homePage     The home page object.
     * @param productDetailsPage The product details page object.
     * @return The total amount of the cart after adding the products.
     */
    public Double addProductsToCartAndReturnTotalAmount(List<String> productNames, HomePage homePage, ProductDetailsPage productDetailsPage){
        int i=0;
        Double totalAmount = Double.valueOf(0);
        List<Locator> allProductsListedOnHomePage = homePage.getAllProductsListedOnHomePage();

        // Loop through each product name and add it to the cart
        for (int j = 0, productNamesLength = productNames.size(); j < productNamesLength; j++) {
            String product = productNames.get(j);

            // Check if the product is displayed on the home page
            if (!homePage.isProductDisplayed(product)) {
                System.out.println("sample products not available");
                product = allProductsListedOnHomePage.get(i).innerText();
            }

            // Click on the product to go to its details page and add it to the cart
            homePage.clickProduct(product);
            totalAmount = Double.sum(totalAmount, productDetailsPage.addProductToCard(productDetailsPage));
            productDetailsPage.clickHome();
            productNames.set(j,product);
        }
        return totalAmount;
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
        return LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern(datePattern));
    }
}
