package cn.gtdec.ncc.service;

import cn.gtdec.ncc.common.AuthService;
import cn.gtdec.ncc.common.Base64Util;
import cn.gtdec.ncc.common.FileUtil;
import cn.gtdec.ncc.common.HttpUtil;
import java.net.URLEncoder;

public class InvoiceServiceImpl {

    public static String vatInvoice() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";
        try {
            // 本地文件路径
            String filePath = "[本地文件路径]";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();
            System.out.println(accessToken);
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String vatInfo = vatInvoice();
    }
}
