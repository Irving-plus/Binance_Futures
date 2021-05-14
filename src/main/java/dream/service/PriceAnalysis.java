package dream.service;

import com.alibaba.fastjson.JSONObject;
import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.Order;
import com.google.common.collect.Collections2;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import dream.constants.PrivateConfig;
import dream.entity.Price;
import dream.entity.Rise;
import dream.wechat.WeChatMsgSend;
import dream.wechat.WeChatUrlData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zxl
 * @version 1.0
 * @date 2021/4/27 16:32
 * 价格分析
 */

public class PriceAnalysis {
    private final static int size =100 ;
    private final static int flagTimes =3 ;
    private final static int curflagTimes =0 ;
    private final static BigDecimal remarkRise = BigDecimal.valueOf(0.03);
    private final static BigDecimal remind = BigDecimal.valueOf(0.005);

    private  static boolean isOrder = false;

    //最近价格队列
    private final static  EvictingQueue<Price> queue = EvictingQueue.create(size);
    //最近价格增长队列
    private final static  EvictingQueue<Rise> riseQueue = EvictingQueue.create(10);

    public static void main(String[] args) {
        SubscriptionClient client = SubscriptionClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY);
        client.subscribeSymbolBookTickerEvent("btcusdt", ((event) -> {
            Price price =new Price();
            price.setTime(System.currentTimeMillis());
            price.setPrice(event.getBestBidPrice());
            price.setSymbol(event.getSymbol());
            queue.add(price);
           // System.out.println(price);
            //client.unsubscribeAll();
            if (queue.size() >= size-1){
                doPrice();
            }
        }), null);

    }
    public static void doPrice(){
        long l = System.currentTimeMillis();
        //开始计算当前
        Map<Long, List<Price>> map = queue.parallelStream().collect(Collectors.groupingBy(a -> a.getTime()));
        queue.clear();
        List<Price> prices = Lists.newArrayList();
        map.values().forEach(a->{
            BigDecimal total = a.stream().map(b -> b.getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg  = total.divide(new BigDecimal(a.size()),4, BigDecimal.ROUND_HALF_UP);
            Price p =  new Price();
            p.setPrice(avg);
            p.setSymbol(a.get(0).getSymbol());
            p.setTime(a.get(0).getTime());
            p.setIndex((int) (l-a.get(0).getTime()));
            prices.add(p);
        });

        if (prices!=null&&prices.size() > 1) {
            prices.sort((o1, o2) -> (int) (o1.getTime()-o2.getTime()));
            long end = System.currentTimeMillis();
            Price endPrice = prices.get(prices.size() - 1);
            Price flag = prices.get(0);

            System.out.println("计算时间差"+(endPrice.getTime()-flag.getTime()));
            System.out.println("计算数量"+prices.size());
            BigDecimal sum = BigDecimal.ZERO;
            //计算平均斜率
            for (int i = 0; i < prices.size(); i++) {
                Price price = prices.get(i);
                BigDecimal divide = price.getPrice().subtract(flag.getPrice()).divide(flag.getPrice(), 8, BigDecimal.ROUND_HALF_UP);
                price.setRiseRate(divide);
                sum= sum.add(divide);
            }
            BigDecimal riseRate = sum.multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(prices.size()), 4, BigDecimal.ROUND_HALF_UP);
            System.out.println("基础数据"+flag.getPrice());
            System.out.println("当前价格"+endPrice.getPrice());
            System.out.println("当前增长率"+riseRate +"%");
            Rise rise = new Rise();
            rise.setSymbol(flag.getSymbol());
            rise.setCountNum(prices.size());
            rise.setCountTime((int) (endPrice.getTime()-flag.getTime()));
            rise.setCurPrice(endPrice.getPrice());
            rise.setRemarkPrice(flag.getPrice());
            rise.setTime(System.currentTimeMillis());
            rise.setRiseRate(riseRate);
            riseQueue.add(rise);
            doRise(endPrice.getPrice());
        }
        //计算最近增长率  调用api开仓

        //System.out.println(prices);
    }
    public static void doRise(BigDecimal price){
        List<Rise> rises = new ArrayList<>(riseQueue);
        BigDecimal reduce = rises.stream().map(b -> b.getRiseRate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal riseRate = reduce.divide(BigDecimal.valueOf(rises.size()), 4, BigDecimal.ROUND_HALF_UP);
        //微信提醒
        if(riseRate.abs().compareTo(remind)>0) {
            String content = "行情提醒" +"\n关注币种：BTC\n当前价格："+price
                    +"\n几秒钟增长率："+riseRate;
            WeChatMsgSend.send(content);
            if (riseRate.compareTo(BigDecimal.ZERO) > 0) {
                content.replace("增长","涨幅");
            }else{
                content.replace("增长","跌幅");
            }
            isOrder = true;
        }
        //立即上车
        if(riseRate.abs().compareTo(remarkRise)>0) {
            //微信提醒
            if (riseRate.compareTo(BigDecimal.ZERO) > 0) {
                //开多
                doOrder(price, OrderSide.BUY, PositionSide.LONG);
            }else{
                //开空
                doOrder(price, OrderSide.SELL, PositionSide.SHORT);
            }
            isOrder = true;
        }
        System.out.println("一段时间平均增长率"+riseRate +"%");
    }
    public static void doOrder(BigDecimal price, OrderSide buy, PositionSide aLong){
        if(!isOrder){
            RequestOptions options = new RequestOptions();
            SyncRequestClient syncRequestClient = SyncRequestClient.create(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY,
                    options);
            Order order = syncRequestClient.postOrder("BTCUSDT",buy, aLong, OrderType.LIMIT, TimeInForce.GTC,
                    "0.005", price.toString(), null, null, null, WorkingType.CONTRACT_PRICE, NewOrderRespType.RESULT);
            System.out.println("开单成功发送消息"+order);
            WeChatMsgSend.send("开单成功"+order);
        }
    }
}
