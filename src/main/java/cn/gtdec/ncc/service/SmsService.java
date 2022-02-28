package cn.gtdec.ncc.service;

import com.alibaba.fastjson.JSONObject;

public interface SmsService {

    public JSONObject sendMessageBySMS(JSONObject json);
}
