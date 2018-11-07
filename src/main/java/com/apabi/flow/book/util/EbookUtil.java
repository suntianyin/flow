package com.apabi.flow.book.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/2 13:47
 * @description
 */
public class EbookUtil {

    private static final Logger log = LoggerFactory.getLogger(EbookUtil.class);

    private static String SERVICE_TYPE_CSS = "htmlcss";

    public static String SERVICE_TYPE_IMAGE = "Imagepage";

    public static String SERVICE_TYPE_HTML = "htmlpage";

    public static String makeCssUrl(String shuyuanOrgId, String metaid) throws Exception {
        String baseUrlType = "command/htmlpage.ashx";
        String serviceType = SERVICE_TYPE_CSS;
        String objId = metaid + ".ft.cebx.1";
        String url = ShuyuanUtil.APABI_CEBXOL + "/" + baseUrlType + "?" + "ServiceType=" + serviceType + "&objID=" + URLEncoder.encode(objId, "UTF-8") + "&metaId=" + URLEncoder.encode(metaid, "UTF-8") + "&OrgId=" + shuyuanOrgId;
        log.info("makeCssUrl[" + url + "]");
        return url;
    }

    //根据类型组装抽图或抽流式url
    public static String makeHtmlUrl(String shuyuanOrgId, String metaid, String baseUrlType, String serviceType, String width, String height, Long pageid) throws Exception {
        String objId = metaid + ".ft.cebx.1";
        String rights = "1-0_00";           //测试用
        String userName = shuyuanOrgId;
        String rightKey = PropertiesUtil.get("iyzhiKey");
        Map<String, Object> signMap = SySignUtils.ebookSign(shuyuanOrgId, userName, metaid, rights, rightKey);
        String url = "";
        if (serviceType.equals(SERVICE_TYPE_IMAGE)) {
            url = ShuyuanUtil.APABI_CEBXOL + "/" + baseUrlType + "?" + "ServiceType=" + serviceType + "&objID=" + URLEncoder.encode(objId, "UTF-8") + "&metaId=" + URLEncoder.encode(metaid, "UTF-8") + "&OrgId=" + shuyuanOrgId + "&username=" + URLEncoder.encode(userName, "UTF-8") + "&rights=" + rights + "&time=" + signMap.get("time") + "&sign=" + signMap.get("sign");
        } else if (serviceType.equals(SERVICE_TYPE_HTML)) {
            url = ShuyuanUtil.APABI_CEBXOL + "/" + baseUrlType + "?" + "ServiceType=" + serviceType + "&objID=" + URLEncoder.encode(objId, "UTF-8") + "&metaId=" + URLEncoder.encode(metaid, "UTF-8") + "&OrgId=" + shuyuanOrgId + "&width=" + width + "&height=" + height + "&pageid=" + pageid + "&username=" + URLEncoder.encode(userName, "UTF-8") + "&rights=" + rights + "&time=" + signMap.get("time") + "&sign=" + signMap.get("sign");
        }
        log.info("makeHtmlUrl[" + url + "]");
        return url;
    }


    //根据类型组装流式分页内容查询url
    public static String makePageUrl(String makePageUrl,String shuyuanOrgId, String metaid, String baseUrlType, String serviceType, String width, String height, Long pageid) throws Exception {
        String objId = metaid + ".ft.cebx.1";
        String rights = "1-0_00";           //测试用
        String userName = shuyuanOrgId;
        String rightKey = PropertiesUtil.get("iyzhiKey");
        Map<String, Object> signMap = SySignUtils.ebookSign(shuyuanOrgId, userName, metaid, rights, rightKey);
        String url = "";
        if (serviceType.equals(SERVICE_TYPE_IMAGE)) {
            url = makePageUrl + "/" + baseUrlType + "?" + "ServiceType=" + serviceType + "&objID=" + URLEncoder.encode(objId, "UTF-8") + "&metaId=" + URLEncoder.encode(metaid, "UTF-8") + "&OrgId=" + shuyuanOrgId + "&username=" + URLEncoder.encode(userName, "UTF-8") + "&rights=" + rights + "&time=" + signMap.get("time") + "&sign=" + signMap.get("sign");
        } else if (serviceType.equals(SERVICE_TYPE_HTML)) {
            url = makePageUrl + "/" + baseUrlType + "?" + "ServiceType=" + serviceType + "&objID=" + URLEncoder.encode(objId, "UTF-8") + "&metaId=" + URLEncoder.encode(metaid, "UTF-8") + "&OrgId=" + shuyuanOrgId + "&width=" + width + "&height=" + height + "&pageid=" + pageid + "&username=" + URLEncoder.encode(userName, "UTF-8") + "&rights=" + rights + "&time=" + signMap.get("time") + "&sign=" + signMap.get("sign");
        }
        log.info("makeHtmlUrl[" + url + "]");
        return url;
    }
}
