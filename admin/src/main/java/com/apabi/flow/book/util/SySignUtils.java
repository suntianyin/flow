package com.apabi.flow.book.util;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/2 13:55
 * @description
 */
public class SySignUtils {

    public final static int DEFAULT_VALID_DAYS = 1;		//默认有效签名天数

    //图书签名
    public static Map<String, Object> ebookSign(String orgId, String userName, String metaId, String rights, String rightKey) throws Exception{
        String time = getTime();
        String source = orgId + "_"+ userName +"_" + metaId + "_"+ rights + "_" + time + "_" + rightKey;
        Map<String, Object> model = makeSign(source,time);
        return model;
    }

    private static String getTime() {
        Date date = DateUtil.getDateAfterDays(new Date(), DEFAULT_VALID_DAYS);
        String time= DateUtil.formatDate(date);
        return time;
    }

    //秘钥生成服务
    private static Map<String, Object> makeSign(String source,String time) throws Exception{
        String sign = new MD5().md5s(source).toUpperCase();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("sign", sign);
        model.put("time", URLEncoder.encode(time, "UTF-8"));
        return model;
    }
}
