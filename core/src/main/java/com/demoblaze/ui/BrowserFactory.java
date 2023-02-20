package com.demoblaze.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.Builder;
import lombok.NonNull;


@Builder
public class BrowserFactory {
    @NonNull private final TestConfiguration config;

    public Browser launchBrowserWithType(Playwright playwright) {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(config.isHeadless());
        TestConfiguration.BrowserName browserName = config.getBrowserName();
        BrowserType browserType;
        switch (browserName) {
            case CHROMIUM:
                browserType = playwright.chromium();
                break;
            case FIREFOX:
                browserType = playwright.firefox();
                break;
            case WEBKIT:
                browserType = playwright.webkit();
                break;
            default:
                throw new IllegalArgumentException("Invalid browser type: " + config.getBrowserName());
        }

        return browserType.launch(options).newContext(new Browser.NewContextOptions().setViewportSize(null)).browser();
    }
}
