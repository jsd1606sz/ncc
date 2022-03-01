package cn.gtdec.ncc.controller;

import cn.gtdec.ncc.service.WlkService;
import cn.gtdec.ncc.service.LybzjService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("履约保证金类")
@RequestMapping("/ncc")
public class LybzjController {
    @Autowired
    private LybzjService lybzjService;

    @ApiOperation(value = "sendLybzj", notes = "推送履约保证金凭证接口")
    @ResponseBody
    @RequestMapping(value = "/sendLybzj", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject queryCustNum(@ApiParam("履约保证金基础信息") @RequestBody JSONObject jsonParamsOrg){
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
                infos = lybzjService.sendLybzj(jsonParams);
            }
        }else{
            infos.put("resultcode","0");
            infos.put("content","");
            infos.put("resultdescription","参数错误");
        }
        return infos;
    }

}
