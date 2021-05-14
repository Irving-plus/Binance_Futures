package com.binance.client.examples.trade;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;

import dream.constants.PrivateConfig;

public class ChangeInitialLeverage {
    public static void main(String[] args) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                options);
        //调整杠杆
        System.out.println(syncRequestClient.changeInitialLeverage("BTCUSDT", 5));
    }
}