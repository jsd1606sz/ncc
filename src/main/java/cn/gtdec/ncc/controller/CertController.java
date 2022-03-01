package cn.gtdec.ncc.controller;

import cn.gtdec.ncc.service.CertService;
import cn.gtdec.ncc.service.CertServiceImpl;
import cn.gtdec.ncc.service.CustServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("凭证查询类")
@RequestMapping("/ncc")
public class CertController {
    @Autowired
    private CertService certService;

    @ApiOperation(value = "queryCertNum", notes = "查询凭证信息接口")
    @ResponseBody
    @RequestMapping(value = "/queryCertNum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject queryCertNum(@ApiParam("获取凭证信息") @RequestBody JSONObject jsonParamsOrg){
        JSONObject jsonParams=jsonParamsOrg.getJSONObject("data");
        System.out.println(jsonParams.toJSONString());
        JSONObject infos = new JSONObject();
        //检查参数docId|billType是否都存在
        if(jsonParams.containsKey("docId") && jsonParams.containsKey("billType")){
            //检查参数docId|billType是否为空
            if(jsonParams.getString("docId").isEmpty() || jsonParams.getString("billType").isEmpty()) {
                infos.put("resultcode","0");
                infos.put("content","");
                infos.put("resultdescription","参数docId|billType不能为空");
            }else{
                infos = certService.getCertNum(jsonParams);
            }
        }else{
            infos.put("resultcode","0");
            infos.put("content","");
            infos.put("resultdescription","参数docId|billType不存在");
        }
        return infos;
    }

}
