package cn.gtdec.ncc.service;

import cn.gtdec.ncc.common.ConnectUtil;
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
public class CertServiceImpl implements CertService{

    @Autowired
    private ConnectUtil conn;

    @Override
    public JSONObject getCertNum(JSONObject json){
        System.out.println(conn.getCertUrl());
        String certUrl = conn.getCertUrl();
        JSONObject remark = new JSONObject();
        String billNo=json.getString("docId");
        String billType=json.getString("billType");
        ClientHttpRequestFactory factory = RestTemplateConfig.simpleClientHttpRequestFactory();
        RestTemplate restTemplate = RestTemplateConfig.restTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json;charset=UTF-8");
        String url = certUrl+"?billNo="+billNo+"&billType="+billType;
        HttpEntity<JSONObject> httpEntity = new HttpEntity(headers);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url,httpEntity,JSONObject.class);
        if(response.getStatusCodeValue() == 200) {
            JSONObject respJSON = response.getBody();
            System.out.println(respJSON.toJSONString());
            String code = respJSON.getString("status");
            String msg = "查询成功";
            String voucherno = "";
            if (code.equals("2000")) {
                msg = "查询成功";
                voucherno = respJSON.getString("voucherno");
            } else {
                msg = respJSON.getString("remark");
                if (msg.length() > 1000) {
                    msg = msg.substring(0, 1000);
                }
            }
            remark.put("resultcode", code);
            remark.put("content", voucherno);
            remark.put("record", msg);
            remark.put("resultdescription", msg);
        }
        return remark;
    }
}
