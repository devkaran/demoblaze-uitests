package com.demoblaze.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

// This method provides the data required to execute the test scenarios using the TestNG data provider annotation.
    @DataProvider
    public Object[][] testDP(){
// Build the TestConfiguration object which contains the configuration details for the test.
        TestConfiguration testConfiguration = TestConfiguration.builder()
                .baseUrl("https://www.demoblaze.com/index.html")
                .timeout(10)
                .browserName(TestConfiguration.BrowserName.CHROMIUM)
                .headless(false)
                .build();

// Build the OrderDetails object which contains the input details for the order form.
        OrderDetails orderDetailsFormInput = OrderDetails.builder()
                .name("Dev")
                .country("India")
                .city("Bengaluru")
                .creditCard("1234567890123456")
                .month("11")
                .year("2025")
                .build();

// Build the BrowserFactory object with the test configuration details.
        BrowserFactory browserFactory = BrowserFactory.builder()
                .config(testConfiguration)
                .build();
// Launch the browser using the browser factory and Playwright.
        Playwright playwright = Playwright.create();
        Browser browser = browserFactory.launchBrowserWithType(playwright);

// Return the test data as a 2D array, where each row represents a set of input parameters to the test method.
        return new Object[][]{
                { 2, testConfiguration, browser, orderDetailsFormInput}
        };
    }
}
