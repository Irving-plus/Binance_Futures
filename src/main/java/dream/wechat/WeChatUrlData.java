package dream.wechat;

import lombok.Data;

/**
 * @author zxl
 * @version 1.0
 * @date 2021/4/28 16:37
 */
@Data
public class WeChatUrlData {

    /**
     *  企业Id
     */
    private String corpid;

    /**
     * secret管理组的凭证密钥
     */
    private String corpsecret;

    /**
     * 获取ToKen的请求
     */
    private String Get_Token_Url;

    /**
     * 发送消息的请求
     */
    private String SendMessage_Url;

    public void setGet_Token_Url(String corpid,String corpsecret) {
        Get_Token_Url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpid+"&corpsecret="+corpsecret;
    }

    public String getSendMessage_Url() {
        SendMessage_Url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        return SendMessage_Url;
    }

}
