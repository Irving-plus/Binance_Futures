package com.binance.client.examples.websocket;

import com.binance.client.SubscriptionClient;
import dream.constants.PrivateConfig;

public class SubscribeAllTicker {

    public static void main(String[] args) {

        SubscriptionClient client = SubscriptionClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY);
   
        client.subscribeAllTickerEvent(((event) -> {
            System.out.println(event);
            client.unsubscribeAll();
        }), null);

    }

}
