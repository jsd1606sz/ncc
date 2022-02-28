package cn.gtdec.ncc.controller;

import cn.gtdec.ncc.service.CustService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("客商接口类")
@RequestMapping("/ncc")
public class CustController {
    @Autowired
    private CustService custService;

    @ApiOperation(value = "queryCustNum", notes = "查询客商编号接口")
    @ResponseBody
    @RequestMapping(value = "/queryCustNum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject queryCustNum(@ApiParam("客商基础信息") @RequestBody JSONObject jsonParams){
        System.out.println(jsonParams.toJSONString());
        JSONObject infos = new JSONObject();
        //检查参数cmpName|supplierclass|iscustomer是否都存在
        if(jsonParams.containsKey("cmpName") && jsonParams.containsKey("supplierclass")
                && jsonParams.containsKey("iscustomer")){
            //检查参数cmpName|supplierclass|iscustomer是否为空
            if(jsonParams.getString("cmpName").isEmpty() || jsonParams.getString("supplierclass").isEmpty()
                    || jsonParams.getString("iscustomer").isEmpty()) {
                infos.put("resultcode","0");
                infos.put("content","");
                infos.put("resultdescription","参数cmpName|supplierclass|iscustomer不能为空");
            }else{
                //检查参数supplierclass是企业还是个人，单位：00101，个人：00102。如个人则税号为空
                if (jsonParams.getString("supplierclass").equals("Y")) {
                    jsonParams.put("supplierclass", "00101");
                } else {
                    jsonParams.put("supplierclass", "00102");
                    jsonParams.put("taxpayerid", "");
                }
                infos = custService.getCustNum(jsonParams);
            }
        }else{
            infos.put("resultcode","0");
            infos.put("content","");
            infos.put("resultdescription","参数cmpName|supplierclass|iscustomer不存在");
        }
        return infos;
    }

}
