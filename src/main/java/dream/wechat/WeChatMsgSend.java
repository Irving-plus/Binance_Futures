package dream.wechat;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxl
 * @version 1.0
 * @date 2021/4/28 16:39
 */
public class WeChatMsgSend {
    private CloseableHttpClient httpClient;

    /**
     * 用于提交登录数据
     */
    private HttpPost httpPost;

    /**
     * 用于获得登陆后页面
     */
    private HttpGet httpGet;

    public static final String CONTENT_TYPE = "Content-Type";

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 微信授权请求，GET类型，获取授权响应，用于其他方法截取token
     *
     * @param Get_Token_Url
     * @return String 授权响应内容
     * @throws IOException
     */
    protected String toAuth(String Get_Token_Url) throws IOException {
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet(Get_Token_Url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String resp = "";

        try {
            HttpEntity entity = response.getEntity();
            resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            response.close();
        }
        LoggerFactory.getLogger(getClass()).info(" resp:{}", resp);
        return resp;
    }

    /**
     * corpid应用组织编号 corpsecret应用秘钥 获取toAuth(String
     * Get_Token_Url)返回结果中键值对中access_token键的值
     *
     * @param
     */
    public String getToken(String corpid, String corpsecret) throws IOException {
        WeChatMsgSend sw = new WeChatMsgSend();
        WeChatUrlData uData = new WeChatUrlData();
        uData.setGet_Token_Url(corpid, corpsecret);
        String resp = sw.toAuth(uData.getGet_Token_Url());
        System.out.println("resp=====:" + resp);
        try {
            Map<String, Object> map = (Map<String, Object>) JSON.parse(resp);
            return map.get("access_token").toString();
        } catch (Exception e) {
            e.getStackTrace();
            return resp;
        }
    }

    /**
     * 创建微信发送请求post数据 touser发送消息接收者 ，msgtype消息类型（文本/图片等）， application_id应用编号。
     * 本方法适用于text型微信消息，contentKey和contentValue只能组一对
     *
     * @param touser
     * @param msgtype
     * @param application_id
     * @param contentKey
     * @param contentValue
     * @return
     */
    public String createpostdata(String touser, String msgtype, int application_id, String contentKey,
                                 String contentValue) {
        WeChatData wcd = new WeChatData();
        wcd.setTouser(touser);
        wcd.setAgentid(application_id + "");
        wcd.setMsgtype(msgtype);
        Map<Object, Object> content = new HashMap<Object, Object>();
        content.put(contentKey, contentValue);
        wcd.setText(content);
        return JSON.toJSONString(wcd);
    }

    /**
     * @Title  创建微信发送请求post实体，charset消息编码    ，contentType消息体内容类型，
     * url微信消息发送请求地址，data为post数据，token鉴权token
     * @param charset
     * @param contentType
     * @param url
     * @param data
     * @param token
     * @return
     * @throws IOException
     */
    public String post(String charset, String contentType, String url, String data, String token) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        httpPost = new HttpPost(url + token);
        httpPost.setHeader(CONTENT_TYPE, contentType);
        httpPost.setEntity(new StringEntity(data, charset));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        String resp;
        try {
            HttpEntity entity = response.getEntity();
            resp = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        LoggerFactory.getLogger(getClass()).info("call [{}], param:{}, resp:{}", url, data, resp);
        return resp;
    }
    public static void send(String mes){
        WeChatMsgSend swx = new WeChatMsgSend();
        try {
            String token = swx.getToken("ww7780a3d5cf51fa41","wu1lRanRRQCxIlU7xldqslNfULjLzvE_PQXiNvtLPAI");
            String postdata = swx.createpostdata("@all", "text",
                    1000002, "content",mes);
            String resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            System.out.println("获取到的token======>" + token);
            System.out.println("请求数据======>" + postdata);
            System.out.println("发送微信的响应数据======>" + resp);
        }catch (Exception e) {
            e.getStackTrace();
        }
    }
    public static void main(String[] args) {
        WeChatMsgSend swx = new WeChatMsgSend();
        try {
            String token = swx.getToken("ww7780a3d5cf51fa41","wu1lRanRRQCxIlU7xldqslNfULjLzvE_PQXiNvtLPAI");
            String postdata = swx.createpostdata("@all", "text",
                    1000002, "content","这是一条测试信息");
            String resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            System.out.println("获取到的token======>" + token);
            System.out.println("请求数据======>" + postdata);
            System.out.println("发送微信的响应数据======>" + resp);
        }catch (Exception e) {
            e.getStackTrace();
        }
    }
}