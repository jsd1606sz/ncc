package cn.gtdec.ncc.controller;

import cn.gtdec.ncc.common.EncodeByMD5;
import cn.gtdec.ncc.service.CertServiceImpl;
import cn.gtdec.ncc.service.SmsService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("短信通知类")
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;

    @ApiOperation(value = "sendSms", notes = "发送短信通知接口")
    @ResponseBody
    @RequestMapping(value = "/sendSms", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject queryCertNum(@ApiParam("发送短信通知") @RequestBody JSONObject jsonParamsOrg){
        JSONObject jsonParams=jsonParamsOrg.getJSONObject("data");
        System.out.println(jsonParams.toJSONString());
        JSONObject infos = new JSONObject();
        //检查参数docId|billType是否都存在
        if(jsonParams.containsKey("mobile") && jsonParams.containsKey("msginfo")){
            //检查参数docId|billType是否为空
            if(jsonParams.getString("mobile").isEmpty() || jsonParams.getString("msginfo").isEmpty()) {
                infos.put("resultcode","0");
                infos.put("content","");
                infos.put("resultdescription","参数mobile|msginfo不能为空");
            }else{
                infos = smsService.sendMessageBySMS(jsonParams);
            }
        }else{
            infos.put("resultcode","0");
            infos.put("content","");
            infos.put("resultdescription","参数mobile|msgInfo不存在");
        }
        return infos;
    }

}
