package dream.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zxl
 * @version 1.0
 * @date 2021/4/27 16:55
 */
@Data
public class Rise {
    //交易对
    private String symbol;
    //当前时间
    private long time;
    //格式化时间
    private long formartTime;
    //当前价格
    private BigDecimal curPrice;
    //当前价格
    private BigDecimal remarkPrice;
    //下标
    private int index;
    //增长率
    private BigDecimal riseRate;
    //计算时间差
    private int countTime;
    //计算数量
    private int countNum;




}

