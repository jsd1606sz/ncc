package cn.gtdec.ncc.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeByMD5 {

    public static String encodeByMD5(String originstr) {
        String password = "";
        if (originstr != null) {
            try {
                // 创建具有指定算法名称的信息摘要
                MessageDigest md = MessageDigest.getInstance("MD5");
                // 使用指定的字节数组对摘要进行最后的更新，然后完成摘要计算
                byte[] results = md.digest(originstr.getBytes());
                // 将得到的字节数组编程字符串返回
                String resultString = byteArrayToHexString(results);
                password = resultString.toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        return password;
    }

    private static String byteArrayToHexString(byte[] results) {
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < results.length; offset++) {
            int i = results[offset];
            if(i<0){
                i += 256;
            }
            if(i<16){
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        String result = buf.toString();
        return result;
    }

}
