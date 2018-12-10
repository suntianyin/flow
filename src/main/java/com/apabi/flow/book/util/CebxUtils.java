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
import java.util.stream.Stream;

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
                List<Element> list = doc.selectNodes("/Return/Result/PageInfo");
                //清空目录章节对应map
                String totalNum = list.get(0).attributeValue("TotalCount");
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

    public static String getStreamCatalog(String metaId,String shuyuanOrgId) {
        try {
            String url = EbookUtil.getCebxPage(metaId, shuyuanOrgId);
            HttpEntity httpEntity = HttpUtils.doGetEntity(url);
            String tmp = EntityUtils.toString(httpEntity);
            tmp=new String(tmp.getBytes("ISO-8859-1"),"utf-8");
            if (StringUtils.isNotBlank(tmp)) {
                SAXReader saxReader = new SAXReader();
                org.dom4j.Document doc = saxReader.read(new ByteArrayInputStream(tmp.getBytes("UTF-8")));
                List<Element> list = doc.selectNodes("/Return/Result/Content/item");
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
                cataRows.setChapterName(element.elementText("item"));
                cataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("Page")));
                String url = element.attributeValue("src");
                cataRows.setUrl(url);
                for (Element child : childE) {
                    createCataTree(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterNum(chapterNum++);
                bookCataRows.setChapterName(element.getText());
                bookCataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("Page")));
                String url = element.attributeValue("src");
                bookCataRows.setUrl(url);
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    public static void main(String[] args) {
        int cebxPage = CebxUtils.getCebxPage("m.20180420-RXJC-DHWY-0030", "iyzhi");
        System.out.println(cebxPage);
        String iyzhi = CebxUtils.getStreamCatalog("m.20180420-RXJC-DHWY-0030", "iyzhi");
        System.out.println(iyzhi);
//        int count = Stream.of(1,2,3).reduce(0,(aaa, a) -> aaa + a);
//        System.out.println(count);
    }
}
