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

/**
 * 履约备注金
 */
@Service
public class LybzjServiceImpl implements LybzjService {
    @Autowired
    private ConnectUtil conn;
    @Override
    public JSONObject sendLybzj(JSONObject json) {
        System.out.println(conn.getSendUrl());
        String wlkUrl = conn.getSendUrl();
        JSONObject remark = new JSONObject();

        ClientHttpRequestFactory factory = RestTemplateConfig.simpleClientHttpRequestFactory();
        RestTemplate restTemplate = RestTemplateConfig.restTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json;charset=UTF-8");
        JSONObject data = creatLybzjInfo(json);
        HttpEntity<JSONObject> httpEntity = new HttpEntity(data, headers);

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


    public JSONObject creatLybzjInfo(JSONObject json){


        JSONObject simple = new JSONObject();
        JSONObject[] bodyInfos = new JSONObject[1];
        JSONObject[] reqs = new JSONObject[1];
        JSONObject map2 = new JSONObject();
        String scomment = "";
        String secondScene="";
        double money = json.getDouble("amount");
        String projectEnginName = json.getString("projectenginName");
        String cmpName = json.getString("payee");
        String bidbondType = json.getString("bidbondtype");
        String orgName= json.getString("orgname");
        if(bidbondType.equals("保函保证金")){
            scomment ="办理绿城装饰工程集团有限公司"+projectEnginName+bidbondType+"("+orgName+")";
        }else{
            scomment ="支付"+cmpName+projectEnginName+bidbondType+"("+orgName+")";
        }
        simple.put("money", money); //付款金额
        simple.put("bidbondType",bidbondType);
        simple.put("nccCode", json.getString("ncccode")); //客商NCC编码
        simple.put("financialNum", json.getString("financialnum")); //项目编码-财务编码
        if(json.getString("outaccount") != null){
            simple.put("inAccount", json.getString("outaccount")); //付款银行账户
        }else{
            simple.put("inAccount", ""); //付款银行账户
        }
        if(json.getString("groupncccod") != null){
            simple.put("groupNccCod", json.getString("groupncccod"));//业务单元NCC编号
        }else{
            simple.put("groupNccCod", json.getString("ncccode"));//客商NCC编号
        }
        simple.put("scomment", scomment);
        bodyInfos = bidBondA(simple);
        map2.put("bodyInfo", bodyInfos);//从表数据
        map2.put("billCode",json.getString("docnum"));//单据编号工程uuid
        map2.put("billdate", json.getString("datestr"));//单据日期
        map2.put("billmaker", json.getString("creator"));//制单人
        map2.put("billperiod", json.getString("month"));//单据会计期间
        map2.put("billyear",json.getString("year"));//单据会计年度
        map2.put("creator",json.getString("creator"));//创建人
        map2.put("def1","zs001");//付款单类型: 履约保证金业务zs001
        map2.put("def3", json.getString("docnum"));//工程管理平台单据号
        map2.put("pk_balatype",json.getString("settlesubjectcode"));//结算方式
        map2.put("scomment", scomment);//摘要
        reqs[0] = map2;
        JSONObject lybzjInfo = new JSONObject();
        lybzjInfo.put("appKey","");
        lybzjInfo.put("sign", "");
        lybzjInfo.put("method","abs.paybill.syncearnast");//方法名
        lybzjInfo.put("timestamp", json.getString("datestr"));//请求时间戳
        lybzjInfo.put("data",reqs);
        return lybzjInfo;
    }

    //履约保证金
    public static JSONObject[] bidBondA(JSONObject map){
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        String def17 = getCapitalType(map.getString("bidbondType"));
        json.put("local_money_de",map.getDouble("money"));//组织本币金额(借方)
        json.put("money_de",map.getDouble("money"));//借方原币金额
        json.put("notax_de", map.getDouble("money"));//借方原币无税金额
        json.put("scomment",map.getString("scomment"));//摘要
        if(map.getString("bidbondType").equals("保函保证金")){
            json.put("pk_subjcode", "0");//收支项目
            json.put("cashitem", "0");//现金流量项目
        }else{
            json.put("pk_subjcode", "29898");//收支项目
            json.put("cashitem", "1124");//现金流量项目
            json.put("def1", map.getString("financialNum"));//工程项目
            json.put("supplier", map.getString("nccCode"));//供应商编号
        }
        json.put("def17",def17);//付款类型
        json.put("payaccount",map.getString("inAccount"));//付款银行账号
        json.put("def26", 0);//应付贷方金额
        array.add(json);
        JSONObject[] bodyInfos = covertJSONArray(array);
        return bodyInfos;
    }
    //获取付款类型
    public static String getCapitalType(String capitalType){
        String def17 = "";
        if(capitalType.equals("履约保证金")){
            def17 = "005";
        }else if(capitalType.equals("质量保证金")){
            def17 = "006";
        }else if(capitalType.equals("保函保证金")){
            def17 = "007";
        }else{
        }
        return def17;
    }
    public static JSONObject[] covertJSONArray(JSONArray array) {
        JSONObject[] arr = new JSONObject[array.size()];
        for(int i=0;i<array.size();i++){
            arr[i] = array.getJSONObject(i);
        }
        return arr;
    }
}
