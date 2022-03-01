package cn.gtdec.ncc.service;

import cn.gtdec.ncc.common.ConnectUtil;
import cn.gtdec.ncc.common.RestTemplateConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class WlkServiceImpl implements WlkService {
    @Autowired
    private ConnectUtil conn;

    @Override
    public JSONObject sendWlk(JSONObject json) {
        System.out.println(conn.getSendUrl());
        String wlkUrl = conn.getSendUrl();
       JSONObject remark = new JSONObject();

        ClientHttpRequestFactory factory = RestTemplateConfig.simpleClientHttpRequestFactory();
        RestTemplate restTemplate = RestTemplateConfig.restTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json;charset=UTF-8");
        JSONObject data=creatWlkInfo(json);
        HttpEntity<JSONObject> httpEntity = new HttpEntity(data,headers);

        ResponseEntity<JSONObject> response = restTemplate.postForEntity(wlkUrl, httpEntity, JSONObject.class);
        if (response.getStatusCodeValue() == 200) {
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

    public JSONObject creatWlkInfo(JSONObject json){
        //        String cmpName = json.getString("cmpName");
        //        String projectEnginName = json.getString("projectEnginName");
        //        String money = json.getString("money");
        JSONObject simple = new JSONObject();
        JSONObject[] bodyInfos = new JSONObject[1];
        JSONObject[] reqs = new JSONObject[1];
        JSONObject map2 = new JSONObject();
        String secondScene = "";
        String cmpName = json.getString("cmpname");
        String projectEnginName = json.getString("projectenginname");
        String scomment ="支付"+cmpName+"往来款"+"("+projectEnginName+")";
        simple.put("cmpName", cmpName); //收款对象
        simple.put("projectEnginName", projectEnginName); //收款对象
        simple.put("money", json.getDouble("money")); //付款金额
        simple.put("nccCode", json.getString("ncccode")); //客商NCC编码
        simple.put("financialNum", json.getString("financialnum")); //项目编码-财务编码
        if(json.getString("outaccount") != null){
            simple.put("inAccount", json.getString("outaccount")); //付款银行账户
        }else{
            simple.put("inAccount", ""); //付款银行账户
        }
        /**
         * 获取系统当前时间
         */
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date1=sdf.format(date);
        Calendar cal = Calendar.getInstance();
        String month=String.valueOf(cal.get(Calendar.MONTH) + 1);
        String year=String.valueOf(cal.get(Calendar.YEAR));
        simple.put("scomment", scomment);
        bodyInfos = commonConvartA(simple);
        map2.put("bodyInfo", bodyInfos);//从表数据
        map2.put("billCode",json.getString("billcode"));//单据编号工程uuid 使用简道云dataid
        map2.put("billdate", date1);//单据日期 取系统当前年月日 yyyy-MM-dd hh:mm:ss
        map2.put("billmaker", json.getString("billmaker"));//制单人 固定（与创建人一致）
        map2.put("billperiod",month);//单据会计期间 取系统当前月
        map2.put("billyear",year);//单据会计年度 取系统当前年
        map2.put("creator",json.getString("creator"));//创建人 固定
        map2.put("def1","zs002");//付款单类型: 往来款付款单zs002
        map2.put("def3", json.getString("docnum"));//工程管理平台单据号 使用表单中最上方业务编号
        map2.put("pk_balatype",json.getString("pk_balatype"));//结算方式
        map2.put("scomment", scomment);//摘要
        reqs[0] = map2;
        JSONObject wlkInfo=new JSONObject();
        wlkInfo.put("appKey","");
        wlkInfo.put("sign", "");
        wlkInfo.put("method","abs.paybill.syncearnast");//方法名
        wlkInfo.put("timestamp", date1);//请求时间戳
        wlkInfo.put("data",reqs);
        System.out.println(wlkInfo.toString());
        return wlkInfo;
    }

    //往来款
    public static JSONObject[] commonConvartA(JSONObject map){
        JSONArray array = new JSONArray();
        String cmpName = map.getString("cmpName");
        String projectEnginName = map.getString("projectEnginName");
        JSONObject json = new JSONObject();
        String scomment ="支付"+cmpName+"往来款"+"("+projectEnginName+")";
        json.put("supplier", map.getString("nccCode"));//供应商编号
        json.put("local_money_de",map.getDouble("money"));//组织本币金额(借方)
        json.put("money_de",map.getDouble("money"));//借方原币金额
        json.put("notax_de", map.getDouble("money"));//借方原币无税金额
        json.put("scomment",scomment);//摘要
        json.put("def17","011");//付款类型: 往来款011
        /*json.put("def1", map.getString("financialNum"));//工程项目*/
        json.put("payaccount",map.getString("inAccount"));//付款银行账号
        //json.put("def26", 0);//应付贷方金额
        json.put("pk_subjcode", "29898");//收支项目
        json.put("cashitem", "1124");//现金流量项目
        array.add(json);
        JSONObject[] bodyInfos = covertJSONArray(array);
        return bodyInfos;
    }

    public static JSONObject[] covertJSONArray(JSONArray array) {
        JSONObject[] arr = new JSONObject[array.size()];
        for(int i=0;i<array.size();i++){
            arr[i] = array.getJSONObject(i);
        }
        return arr;
    }
}
