package com.binance.client.examples.websocket;

import com.binance.client.SubscriptionClient;
import dream.constants.PrivateConfig;

public class SubscribeAllLiquidationOrder {

    public static void main(String[] args) {

        SubscriptionClient client = SubscriptionClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY);
   
        client.subscribeAllLiquidationOrderEvent(((event) -> {
            System.out.println(event);
        }), null);

    }

}
