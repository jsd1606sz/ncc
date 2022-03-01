package cn.gtdec.ncc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ncc")
public class ConnectUtil {

    @Value("${ncc.config.url}")
    private String url;
    @Value("${ncc.config.certUrl}")
    private String certUrl;
    @Value("${ncc.config.smsUrl}")
    private String smsUrl;
    @Value("${ncc.config.sendUrl}")
    private String sendUrl;

    @RequestMapping("/getUrl")
    public String getUrl(){
        String url = this.url;
        return url;
    }

    @RequestMapping("/getCertUrl")
    public String getCertUrl(){
        String certUrl = this.certUrl;
        return certUrl;
    }

    @RequestMapping("/getSmsUrl")
    public String getSmsUrl(){
        String smsUrl = this.smsUrl;
        return smsUrl;
    }

    @RequestMapping("/getSendUrl")
    public String getSendUrl(){
        String sendUrl = this.sendUrl;
        return sendUrl;
    }
}
