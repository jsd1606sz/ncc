package cn.gtdec.ncc.service;

import cn.gtdec.ncc.common.ConnectUtil;
import cn.gtdec.ncc.common.EncodeByMD5;
import cn.gtdec.ncc.common.RestTemplateConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsServiceImpl implements SmsService{
    @Autowired
    private ConnectUtil conn;

    @Override
    public JSONObject sendMessageBySMS(JSONObject json){
        JSONObject info = new JSONObject();
        String password = EncodeByMD5.encodeByMD5("123456");
        System.out.println("密码123456通过MD5加密后为:"+password);
        String msgInfo = json.getString("msgInfo");
        String msgContent = "【绿城装饰】审批完结:" + msgInfo;
        String mobile = json.getString("mobile");
        String url = conn.getSmsUrl()+"?action=send&userid=&account=gtlczs&password="+password
                +"&mobile="+mobile+"&content="+msgContent+"&sendTime=&extno=";
        ClientHttpRequestFactory factory = RestTemplateConfig.simpleClientHttpRequestFactory();
        RestTemplate restTemplate = RestTemplateConfig.restTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json;charset=UTF-8");
        HttpEntity<JSONObject> httpEntity = new HttpEntity(headers);
        JSONObject sTotalString = new JSONObject();
        try {
            ResponseEntity<JSONObject> responseEntity =
                    restTemplate.postForEntity(url,httpEntity, JSONObject.class);
            sTotalString = responseEntity.getBody();
            System.out.println("sTotalString:"+sTotalString);
            if(responseEntity.getStatusCodeValue() == 200){
                info.put("content", sTotalString.getString("taskID"));
                info.put("record", "短信发送成功");
                info.put("resultdescription", sTotalString.toJSONString());
                info.put("resultcode", "1");
            }else{
                info.put("content", sTotalString.getString("taskID"));
                info.put("record", "短信发送失败");
                info.put("resultdescription", sTotalString.getString("message"));
                info.put("resultcode", ""+responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            info.put("content", "");
            info.put("record", "调用异常");
            info.put("resultdescription", e.getMessage());
            info.put("resultcode", "0");
        }
        return info;
    }
}
