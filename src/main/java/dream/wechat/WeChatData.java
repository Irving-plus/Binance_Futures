package dream.wechat;

import lombok.Data;

/**
 * @author zxl
 * @version 1.0
 * @date 2021/4/28 16:34
 */
@Data
public class WeChatData {
    /**
     * 成员账号
     */
    private String touser;

    /**
     * 消息类型
     */
    private String msgtype;

    /**
     * 企业用用的agentid
     */
    private String agentid;

    /**
     * 十几接收map类型数据
     */
    private Object text;
}
