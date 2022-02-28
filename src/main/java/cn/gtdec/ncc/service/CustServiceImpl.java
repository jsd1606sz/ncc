package cn.gtdec.ncc.service;

import cn.gtdec.ncc.common.ConnectUtil;
import cn.gtdec.ncc.common.RestTemplateConfig;
import com.alibaba.fastjson.JSONObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import java.io.*;


@Service
public class CustServiceImpl implements CustService {

    @Autowired
    private ConnectUtil conn;

    @Override
    public JSONObject getCustNum(JSONObject json){
        System.out.println(conn.getUrl());
        String url = conn.getUrl();
        String data = getXMLString(json.getString("cmpName"),json.getString("supplierclass"),json.getString("iscustomer"),json.getString("taxpayerid"));
        JSONObject infos = null;
        try {
            infos = pushSupper(url,data);
        }catch (Exception e){
            infos.put("content", "");
            infos.put("record", "调用异常");
            infos.put("resultdescription", e.getMessage());
            infos.put("resultcode", 0);
        }
        return infos;
    }

    /**
     * 组装请求参数
     * @param cmpName
     * @param supplierclass
     * @param iscustomer
     * @param taxpayerid
     * @return
     */
    public static String getXMLString(String cmpName,String supplierclass,String iscustomer,String taxpayerid) {
        String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        StringBuffer sb = new StringBuffer();
        sb.append(XML_HEADER);
        sb.append("<ufinterface account=\"001\" billtype=\"supplier02\" filename=\"\" groupcode=\"\" isexchange=\"Y\" replace=\"Y\" roottag=\"\" sender=\"sup\">");
        sb.append("<bill id=\"\">");
        sb.append("<billhead>");
        sb.append("<pk_group>001</pk_group>");
        sb.append("<pk_org>001</pk_org>");
        sb.append("<code></code>");
        sb.append("<name>"+ cmpName +"</name>");
        sb.append("<shortname></shortname>");
        sb.append("<pk_supplierclass>"+ supplierclass +"</pk_supplierclass>");
        sb.append("<pk_areacl>010160104</pk_areacl>");
        sb.append("<mnecode></mnecode>");
        sb.append("<iscustomer>"+ iscustomer +"</iscustomer>");
        sb.append("<corcustomer></corcustomer>");
        sb.append("<isfreecust>N</isfreecust>");
        sb.append("<supprop>0</supprop>");
        if(taxpayerid == null || taxpayerid.isEmpty() || "".equals(taxpayerid)){
            sb.append("<taxpayerid></taxpayerid>");
        }else{
            sb.append("<taxpayerid>"+ taxpayerid +"</taxpayerid>");
        }
        sb.append("<pk_financeorg></pk_financeorg>");
        sb.append("<pk_country>CN</pk_country>");
        sb.append("<enablestate>2</enablestate>");
        sb.append("<pk_timezone>P0800</pk_timezone>");
        sb.append("<pk_format>ZH-CN</pk_format>");
        sb.append("</billhead>");
        sb.append("</bill>");
        sb.append("</ufinterface>");
        System.out.println(sb.toString());
        // 返回String格式
        return sb.toString();
    }

    /**
     * 执行接口调用
     * @param data
     * @param url
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static JSONObject pushSupper(String url,String data) throws IOException, Exception {
        JSONObject info = new JSONObject();
        ResponseEntity<String> responseEntity = null;
        ClientHttpRequestFactory factory = RestTemplateConfig.simpleClientHttpRequestFactory();
        RestTemplate restTemplate = RestTemplateConfig.restTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "text/html;charset=UTF-8");
        headers.set("Accept", "text/html;charset=UTF-8");
        String sTotalString=null;
        try {
            responseEntity = restTemplate.postForEntity(url,
                    new HttpEntity<String>(data, headers), String.class);
            sTotalString = responseEntity.getBody();
            if(responseEntity.getStatusCodeValue() == 200){
                info = putInfos(sTotalString);
            }else{
                info.put("content", "");
                info.put("record", "调用异常");
                info.put("resultdescription", "调用异常");
                info.put("resultcode", "0");
            }
        } catch (Exception e) {
            info.put("content", "");
            info.put("record", "调用异常");
            info.put("resultdescription", e.getMessage());
            info.put("resultcode", "0");
        }
        return info;
    }

    /**
     * 加工响应报文
     * @param resp
     * @return JOSNObject
     * @throws Exception
     */
    public static JSONObject putInfos(String resp) throws Exception{
        JSONObject info = new JSONObject();
        if(!resp.isEmpty()) {
            StringReader read = new StringReader(resp);
            InputSource source = new InputSource(read);
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(source);
            Element root = doc.getRootElement().getChild("sendresult");
            String resultcode = root.getChildText("resultcode");
            String resultdescription = root.getChildText("resultdescription");
            String content = root.getChildText("content");
            System.out.println("resultcode" + resultcode + "resultdescription" + resultdescription + "content" + content);
            info.put("resultcode", resultcode);
            info.put("resultdescription", resultdescription);
            if (content.length() > 1000) {
                content = content.substring(0, 1000);
            }
            info.put("record", content);
            if ("1".equals(resultcode)) {
                JSONObject remark = JSONObject.parseObject(content);
                info.put("content", remark.getString("remark"));
            } else {
                info.put("content", "");
            }
        }
        return info;
    }

}
