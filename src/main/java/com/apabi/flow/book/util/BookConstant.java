package com.apabi.flow.book.util;

import java.util.regex.Pattern;

/**
 * @author guanpp
 * @date 2018/5/14 16:17
 * @description
 */
public class BookConstant {
    //缩略图宽度
    public static final int imgWidth = 450;
    //缩略图高度
    public static final int imgHeigth = 600;

    //缩略图宽度
    public static final int imgH = 200;
    //缩略图高度
    public static final int imgW = 200;

    //缩略图宽度
    public static final int imgWgth = 180;
    //缩略图高度
    public static final int imgHgth = 240;

    //cebx屏幕宽度
    public static final int CEBX_WIDTH = 1080;
    //cebx屏幕高度
    public static final int CEBX_HEIGTH = 1080;

    //配置路径
    public static final String BASE_URL = "https://iyuezhi.apabi.com:8443/file/book";
    //样式文件存储路径
    //public static final String CSS_URL = "/apabi/flow/webapps/file/book";
    //public static final String CSS_URL_WIN = "F:/style";

    //文件上传路径（项目相对路径）
    public static final String UPLOAD_FILE_PATH = "/upload/file";
    //cebx解析目标文件夹
    //public static final String TARGET_CEBX_DIR = "F:/cebxHtml";
    //cebx文件名分隔符
    public static final String FILE_NAME_LABEL = "@apabi#";
    //cebx样式文件名
    public static final String CEBX_STYLE_NAME = "style.css";
    //cebx样式文件夹
    public static final String CEBX_STYLE_DIR = "styles";

    //图片路径
    public static final String coverUrl = "http://www.apabi.com/apaDlibrary/GetJpgUrl.aspx?type=1&orgid=apabi&resid=";
    //章节分片大小
    public static final int shardSize = 10;

    //编码格式
    public static final String CODE_UTF8 = "UTF-8";
    public static final String CODE_GBK = "GBK";

    //服务平台
    public static final String PLATFORM = "aiduaikan";
    public static final String LIBID = "bjsw";
    public static final String ROUTE = "shard1";
    public static final String OPTIMIZED = "true";
    public static final String ORIGINAL = "true";

    //isbn正则
    public static final Pattern REG_ISBN1 = Pattern.compile("(97[89][-\\s]?[0-9][-\\s]?[0-9]{2}[-\\s]?[0-9]{6}[-\\s]?[0-9])");

    public static final Pattern REG_ISBN2 = Pattern.compile("(97[89][-\\s]?[0-9][-\\s]?[0-9]{3}[-\\s]?[0-9]{5}[-\\s]?[0-9])");

    public static final Pattern REG_ISBN3 = Pattern.compile("(97[89][-\\s]?[0-9][-\\s]?[0-9]{4}[-\\s]?[0-9]{4}[-\\s]?[0-9])");

    public static final Pattern REG_ISBN4 = Pattern.compile("(97[89][-\\s]?[0-9][-\\s]?[0-9]{5}[-\\s]?[0-9]{3}[-\\s]?[0-9])");

    public static final Pattern REG_ISBN5 = Pattern.compile("([0-9][-\\s]?[0-9]{2}[-\\s]?[0-9]{6}[-\\s]?[0-9x])");

    public static final Pattern REG_ISBN6 = Pattern.compile("([0-9][-\\s]?[0-9]{3}[-\\s]?[0-9]{5}[-\\s]?[0-9x])");

    public static final Pattern REG_ISBN7 = Pattern.compile("([0-9][-\\s]?[0-9]{4}[-\\s]?[0-9]{4}[-\\s]?[0-9x])");

    public static final Pattern REG_ISBN8 = Pattern.compile("([0-9][-\\s]?[0-9]{5}[-\\s]?[0-9]{3}[-\\s]?[0-9x])");
    //public static Pattern REG_ISBN4 = Pattern.compile("(97[89][-\\s]?[0-9][-\\s]?[0-9]{5}[-\\s]?[0-9]{3}[-\\s]?[0-9])|([0-9][-\\s]?[0-9]{5}[-\\s]?[0-9]{3}[-\\s]?[0-9x])");

    //公众号和qq正则
    public static final Pattern DETECT_SOURCE = Pattern.compile("([\\s\\S]{0,10}公众号[\\s\\S]{0,10})|([\\s\\S]{0,10}(?i:qq)((:|：| )?)[\\d]{5,11}[\\s\\S]{5,11})");
}
