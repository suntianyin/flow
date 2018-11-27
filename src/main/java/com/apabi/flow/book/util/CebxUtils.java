package com.apabi.flow.book.util;

import com.apabi.flow.book.model.BookCataRows;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author: sunty
 * @date: 2018/11/23 14:29
 * @description:
 */

public class CebxUtils {
    public static int getCebxPage(String metaId, String shuyuanOrgId) {
        try {
            String cebxPage = EbookUtil.getCebxPage(metaId, shuyuanOrgId);
            HttpEntity httpEntity = HttpUtils.doGetEntity(cebxPage);
            String tmp = EntityUtils.toString(httpEntity);
            int page = 0;
            if (StringUtils.isNotBlank(tmp)) {
                SAXReader saxReader = new SAXReader();
                org.dom4j.Document doc = saxReader.read(new ByteArrayInputStream(tmp.getBytes("UTF-8")));
                List<Element> list = doc.selectNodes("/Return/Result/Content");
                //清空目录章节对应map
                String totalNum = list.get(0).attributeValue("TotalNum");
                page = Integer.parseInt(totalNum);
                return page;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    private static int chapterNum = 0;

    public static String getStreamCatalog(String metaId) {
        try {
            String url = "http://www.apabi.com/apadlibrary/iyzhi/ServiceEntry/Mobile.aspx?api=bookdetail&metaid=" + metaId + "&type=1&multiplePart=0&orgcode=iyzhi";
            HttpEntity httpEntity = HttpUtils.doGetEntity(url);
            String tmp = EntityUtils.toString(httpEntity);
            if (StringUtils.isNotBlank(tmp)) {
                SAXReader saxReader = new SAXReader();
                org.dom4j.Document doc = saxReader.read(new ByteArrayInputStream(tmp.getBytes("UTF-8")));
                List<Element> list = doc.selectNodes("/Return/Record/catalogRow");
                BookCataRows root = new BookCataRows();
                //清空目录章节对应map
                for (Element element : list) {
                    createCataTree(element, root);
                }
                //目录结构树
                JSONArray json = JSONArray.fromObject(root.getChildren());
                chapterNum=0;
                return json.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    //构建目录树
    private static void createCataTree(Element element, BookCataRows parentCata) {
        if (element != null) {
            List<Element> childE = element.elements();
            if (childE != null && childE.size() > 0) {
                BookCataRows cataRows = new BookCataRows();
                cataRows.setChapterNum(chapterNum++);
                cataRows.setChapterName(element.attributeValue("chapterName"));
                cataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("ebookPageNum")));
                String url = element.attributeValue("src");
                cataRows.setUrl(url);
                for (Element child : childE) {
                    createCataTree(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterNum(chapterNum++);
                bookCataRows.setChapterName(element.attributeValue("chapterName"));
                bookCataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("ebookPageNum")));
                String url = element.attributeValue("src");
                bookCataRows.setUrl(url);
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }
}
