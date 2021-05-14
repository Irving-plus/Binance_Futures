package com.binance.client.examples.market;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;

import dream.constants.PrivateConfig;

public class Get24hrTickerPriceChange {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        System.out.println(syncRequestClient.get24hrTickerPriceChange("BTCUSDT"));
        // System.out.println(syncRequestClient.get24hrTickerPriceChange(null));
    }
}
