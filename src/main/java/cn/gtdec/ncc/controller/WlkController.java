package cn.gtdec.ncc.controller;

import cn.gtdec.ncc.service.CustService;
import cn.gtdec.ncc.service.WlkService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("往来款类")
@RequestMapping("/ncc")
public class WlkController {
    @Autowired
    private WlkService wlkService;

    @ApiOperation(value = "sendWlk", notes = "推送往来款凭证接口")
    @ResponseBody
    @RequestMapping(value = "/sendWlk", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject queryCustNum(@ApiParam("往来款基础信息") @RequestBody JSONObject jsonParamsOrg){
        JSONObject jsonParams=jsonParamsOrg.getJSONObject("data");
        System.out.println(jsonParams.toJSONString());
        JSONObject infos = new JSONObject();
        //检查参数cmpName|supplierclass|iscustomer是否都存在
        if(jsonParams.containsKey("cmpName") && jsonParams.containsKey("projectEnginName")
                && jsonParams.containsKey("money")){
            //检查参数cmpName|supplierclass|iscustomer是否为空
            if(jsonParams.getString("cmpName").isEmpty() || jsonParams.getString("projectEnginName").isEmpty()
                    || jsonParams.getString("money").isEmpty()) {
                infos.put("resultcode","0");
                infos.put("content","");
                infos.put("resultdescription","参数不能为空");
            }else{
                infos = wlkService.sendWlk(jsonParams);
            }
        }else{
            infos.put("resultcode","0");
            infos.put("content","");
            infos.put("resultdescription","参数错误");
        }
        return infos;
    }

}
