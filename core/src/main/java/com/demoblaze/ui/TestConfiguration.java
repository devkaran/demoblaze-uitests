package com.demoblaze.ui;


import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class TestConfiguration {
    @NonNull
    private BrowserName browserName;
    @NonNull
    private String baseUrl;
    @NonNull
    private int timeout;
    @NonNull
    private boolean headless;
    private String deviceName;


    enum BrowserName{
        CHROMIUM, FIREFOX, WEBKIT
    }
}
