package com.apabi.flow.book.util;

import com.apabi.flow.book.model.*;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.shuyuan.book.model.SCmfDigitObject;
import com.apabi.shuyuan.book.model.SCmfDigitResfileSite;
import com.apabi.shuyuan.book.model.SCmfMeta;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.siegmann.epublib.domain.TOCReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocument;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/4/24 10:35
 * @description
 */
public class BookUtil {

    private static final Logger log = LoggerFactory.getLogger(BookUtil.class);

    private static String LIB_ID = "bjsw";

    private static String ROUTE = "shard1";

    //删除文件
    public static boolean deleteFile(String path) {
        if (path != null && path.length() > 0) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                file.delete();
                log.info("删除上传文件：" + file.getName() + "成功！");
                log.info("删除路径为：" + path);
                return true;
            }
        } else {
            log.debug("检查路径是否正确！");
        }
        return false;
    }

    //获取三级目录
    public static List<BookCataRows> getThirdCata(List<TOCReference> tocs) {
        if (tocs != null && tocs.size() > 0) {
            List<BookCataRows> catalogList = new ArrayList<>();
            for (TOCReference toc1 : tocs) {
                BookCataRows catalog1 = new BookCataRows();
                catalog1.setChapterName(toc1.getTitle());
                catalog1.setUrl(toc1.getResource().getHref());
                catalogList.add(catalog1);
                List<TOCReference> toc1s = toc1.getChildren();
                //第二级目录
                for (TOCReference toc2 : toc1s) {
                    BookCataRows catalog2 = new BookCataRows();
                    catalog2.setChapterName(toc2.getTitle());
                    catalog2.setUrl(toc2.getResource().getHref());
                    catalogList.add(catalog2);
                    List<TOCReference> toc2s = toc2.getChildren();
                    //第三级目录
                    for (TOCReference toc3 : toc2s) {
                        BookCataRows catalog3 = new BookCataRows();
                        catalog3.setChapterName(toc3.getTitle());
                        catalog3.setUrl(toc3.getResource().getHref());
                        catalogList.add(catalog3);
                    }
                }
            }
            return catalogList;
        }
        return null;
    }

    //获取二级目录
    public static List<BookCataRows> getSecondCata(List<TOCReference> tocs) {
        if (tocs != null && tocs.size() > 0) {
            List<BookCataRows> catalogList = new ArrayList<>();
            for (TOCReference toc1 : tocs) {
                BookCataRows catalog1 = new BookCataRows();
                catalog1.setChapterName(toc1.getTitle());
                catalog1.setUrl(toc1.getResource().getHref());
                catalogList.add(catalog1);
                List<TOCReference> toc1s = toc1.getChildren();
                //第二级目录
                for (TOCReference toc2 : toc1s) {
                    BookCataRows catalog2 = new BookCataRows();
                    catalog2.setChapterName(toc2.getTitle());
                    catalog2.setUrl(toc2.getResource().getHref());
                    catalogList.add(catalog2);
                }
            }
            return catalogList;
        }
        return null;
    }

    //获取一级目录
    public static List<BookCataRows> getFirstCata(List<TOCReference> tocs) {
        if (tocs != null && tocs.size() > 0) {
            List<BookCataRows> catalogList = new ArrayList<>();
            for (TOCReference toc1 : tocs) {
                BookCataRows catalog1 = new BookCataRows();
                catalog1.setChapterName(toc1.getTitle());
                catalog1.setUrl(toc1.getResource().getHref());
                catalogList.add(catalog1);
            }
            return catalogList;
        }
        return null;
    }

    // 处理该路径下的xml
    public static Document getXmlSourceFromUrl(String path) throws Exception {
        if (path != null && path.length() > 0) {
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            if (suffix.toLowerCase().equals("xml")) {
                SAXReader saxReader = new SAXReader();
                Document doc = saxReader.read(new File(path));
                Element root = doc.getRootElement();
                formatFileInfo(root);
                formatCatelog(root);
                formatCategory(root);
                Element libElement = root.addElement("libId");
                libElement.addText(LIB_ID);
                Element routeElement = root.addElement("_route_");
                routeElement.addText(ROUTE);
                //return doc.asXML().trim();
                return doc;
            }
        }
        return null;
    }

    // 处理该路径下的xml
    public static Document getXmlSourceFromRemoteUrl(String url) throws Exception {
        log.debug("获取图书目录 xml 资源， url={}", url);
        Document doc = null;
        if (StringUtils.isNotBlank(url)) {
            HttpEntity httpEntity = null;
            String tmp = "";
            try {
                httpEntity = HttpUtils.doGetEntity(url);
                tmp = EntityUtils.toString(httpEntity);
                doc = DocumentHelper.parseText(tmp);
            } catch (SocketTimeoutException e) {
                log.error("获取图书目录 xml 资源 异常 ， 异常信息＝{}", e);
            }
            /*String suffix = url.substring(url.lastIndexOf(".") + 1);
            if (suffix.toLowerCase().equals("xml")) {
                SAXReader saxReader = new SAXReader();
                Document doc = saxReader.read(new File(path));
                Element root = doc.getRootElement();
                formatFileInfo(root);
                formatCatelog(root);
                formatCategory(root);
                Element libElement = root.addElement("libId");
                libElement.addText(LIB_ID);
                Element routeElement = root.addElement("_route_");
                routeElement.addText(ROUTE);
                //return doc.asXML().trim();
                return doc;
            }*/
        }
        return doc;
    }

    private static void formatCategory(Element root) {
        Node node = root.selectSingleNode("//metadata/ClassCode");
        String classCode = node.getText().trim();
        Element cIdsEle = root.addElement("categoryIds");
        Set<String> categorySet = new HashSet<String>();
        String[] codes = classCode.split("\\s+");
        /*for (String code : codes) {
            String categoryId = codeMap.get(code);
            if (StringUtils.isEmpty(categoryId)) {
                code = code.split("\\.|=|-|\\+|/")[0];
                categoryId = codeMap.get(code);
                if (StringUtils.isEmpty(categoryId)) {
                    Integer length = code.length();
                    for (int i = length; i > 2; i--) {
                        code = code.substring(0, i - 1);
                        categoryId = codeMap.get(code);
                        if (StringUtils.isNotEmpty(categoryId)) {
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isNotEmpty(categoryId)) {
                categorySet.add(categoryId);
            }
        }*/
        if (categorySet.size() == 0) {
            categorySet.add("default");
        }
        for (String categoryId : categorySet) {
            Element cIdEle = cIdsEle.addElement("categoryId");
            cIdEle.addText(categoryId);
        }
    }

    private static void formatCatelog(Element root) {
        @SuppressWarnings("unchecked")
        List<Element> list = (List<Element>) root.selectNodes("//Catalog/catalogRow");
        Element catalogsEle = root.addElement("catalogs");
        for (Element element : list) {
            Map<String, String> map = new HashMap<String, String>();
            String ebookPageNum = element.attributeValue("ebookPageNum");
            map.put("ebookPageNum", ebookPageNum);
            String chapterName = element.attributeValue("chapterName");
            map.put("chapterName", chapterName);
            JSONObject jsonStr = JSONObject.fromObject(map);
            Element catalogEle = catalogsEle.addElement("catalog");
            catalogEle.setText(jsonStr.toString());
        }
    }

    private static void formatFileInfo(Element root) {
        @SuppressWarnings("unchecked")
        List<Element> list = (List<Element>) root.selectNodes("//fileSec/file");
        Element filesEle = root.addElement("files");
        for (Element element : list) {
            Map<String, String> map = new HashMap<String, String>();
            String fileName = element.attributeValue("FileName");
            map.put("fileName", fileName);
            String filePath = element.attributeValue("FilePath");
            map.put("filePath", filePath);
            String order = element.attributeValue("Order");
            map.put("order", order);
            String objID = element.selectSingleNode("./id/ObjID").getText();
            map.put("objID", objID);
            String fileSize = element.selectSingleNode("./techMD/FileSize").getText();
            map.put("fileSize", fileSize);
            String format = element.selectSingleNode("./techMD/Format").getText();
            map.put("format", format);
            if ("cebx".equalsIgnoreCase(format)) {
                Element objIDEle = root.addElement("cebxObjID");
                objIDEle.setText(objID);
                Element fileSizeEle = root.addElement("cebxFileSize");
                fileSizeEle.setText(fileSize);
                String page = element.selectSingleNode("./techMD/Page").getText();
                Element pageEle = root.addElement("cebxPage");
                pageEle.setText(page);
                map.put("page", page);
            } else if ("jpg".equalsIgnoreCase(format)) {
                Element objIDEle = root.addElement("coverObjID");
                objIDEle.setText(objID);
                Element fileSizeEle = root.addElement("coverFileSize");
                fileSizeEle.setText(fileSize);
                String imgWidth = element.selectSingleNode("./techMD/ImgWidth").getText();
                Element imgWidthEle = root.addElement("coverImgWidth");
                imgWidthEle.setText(imgWidth);
                String imgHeigth = element.selectSingleNode("./techMD/ImgHeigth").getText();
                Element imgHeigthEle = root.addElement("coverImgHeigth");
                imgHeigthEle.setText(imgHeigth);
//				String coverUrl = coverPathMap.get(fileName);
//				Element coverUrlEle = root.addElement("coverUrl");
//				coverUrlEle.setText(coverUrl);
            }
            JSONObject jsonStr = JSONObject.fromObject(map);
            Element fileEle = filesEle.addElement("file");
            fileEle.setText(jsonStr.toString());
        }
    }

    //将document转化成图书meta数据
    public static List docToBookMeta(Document doc) {
        if (doc != null) {
            //获取目录数据
            List<Element> cataList = (List<Element>) doc.selectNodes("//Catalog/catalogRow");
            return cataList;
        }
        return null;
    }

    //将从接口中获取的 xml 转成 document后i，再转化成图书meta数据
    public static List remoteDocToBookMeta(Document doc) {
        if (doc != null) {
            //获取目录数据
            List<Element> cataList = (List<Element>) doc.selectNodes("//Record/catalogRow");
            return cataList;
        }
        return null;
    }

    //生成流式服务元数据
    public static BookMeta createBookMeta(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            BookMeta bookMeta = new BookMeta();
            bookMeta.setMetaId(sCmfMeta.getIdentifier());
            bookMeta.setDrId(sCmfMeta.getDrid());
            bookMeta.setTitle(sCmfMeta.getTitle());
            bookMeta.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            bookMeta.setCreator(sCmfMeta.getCreator());
            bookMeta.setSubject(sCmfMeta.getSubject());
            bookMeta.setAbstract_(sCmfMeta.getAbstract());
            bookMeta.setPublisher(sCmfMeta.getPublisher());
            bookMeta.setType(sCmfMeta.getType());
            bookMeta.setContributor(sCmfMeta.getContributor());
            bookMeta.setIssuedDate(sdf.format(sCmfMeta.getIssuedDate()));
            bookMeta.setIdType(sCmfMeta.getIdType());
            bookMeta.setRelation(sCmfMeta.getRelation());
            bookMeta.setRelationType(sCmfMeta.getRelationType());
            bookMeta.setEbookPrice(String.valueOf(sCmfMeta.getPrice()));
            bookMeta.setPaperPrice(String.valueOf(sCmfMeta.getPaperPrice()));
            bookMeta.setForeignPrice(String.valueOf(sCmfMeta.getForeignPrice()));
            bookMeta.setForeignPriceType(sCmfMeta.getForeignPriceType());
            bookMeta.setEditionOrder(sCmfMeta.getEditionOrder());
            bookMeta.setEditionNote(sCmfMeta.getEditionNote());
            bookMeta.setPressOrder(sCmfMeta.getPressOrder());
            bookMeta.setContentNum(sCmfMeta.getContentNum());
            bookMeta.setClassCode(sCmfMeta.getClassCode());
            bookMeta.setLanguage(sCmfMeta.getLanguage());
            bookMeta.setApabiClass(sCmfMeta.getApabiClass());
            bookMeta.setIllustration(sCmfMeta.getIllustration());
            bookMeta.setReditor(sCmfMeta.getReditor());
            bookMeta.setNotes(sCmfMeta.getNotes());
            String isbn = sCmfMeta.getRealISBN();
            bookMeta.setIsbn(isbn);
            if (StringUtils.isNotBlank(isbn)) {
                isbn = isbn.replace("-", "");
                if (isbn.length() == 10) {
                    bookMeta.setIsbn10(isbn);
                } else if (isbn.length() == 13) {
                    bookMeta.setIsbn13(isbn);
                }
            }
            bookMeta.setPlace(sCmfMeta.getPlace());
            //默认值
            bookMeta.setHasCebx(0);
            bookMeta.setIsReadEpub(0);
            bookMeta.setIsReadCebxFlow(0);
            bookMeta.setCreateTime(new Date());
            return bookMeta;
        }
        return null;
    }

    //生成流式服务元数据，temp表
    public static ApabiBookMetaDataTemp createBookMetaTemp(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ApabiBookMetaDataTemp metaDataTemp = new ApabiBookMetaDataTemp();
            metaDataTemp.setMetaId(sCmfMeta.getIdentifier());
            metaDataTemp.setDrId(sCmfMeta.getDrid());
            metaDataTemp.setTitle(sCmfMeta.getTitle());
            metaDataTemp.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            metaDataTemp.setCreator(sCmfMeta.getCreator());
            metaDataTemp.setSubject(sCmfMeta.getSubject());
            metaDataTemp.setAbstract_(sCmfMeta.getAbstract());
            metaDataTemp.setPublisher(sCmfMeta.getPublisher());
            metaDataTemp.setType(sCmfMeta.getType());
            metaDataTemp.setContributor(sCmfMeta.getContributor());
            metaDataTemp.setIssuedDate(sdf.format(sCmfMeta.getIssuedDate()));
            metaDataTemp.setIdType(sCmfMeta.getIdType());
            metaDataTemp.setRelation(sCmfMeta.getRelation());
            metaDataTemp.setRelationType(sCmfMeta.getRelationType());
            metaDataTemp.setEbookPrice(String.valueOf(sCmfMeta.getPrice()));
            metaDataTemp.setPaperPrice(String.valueOf(sCmfMeta.getPaperPrice()));
            metaDataTemp.setForeignPrice(String.valueOf(sCmfMeta.getForeignPrice()));
            metaDataTemp.setForeignPriceType(sCmfMeta.getForeignPriceType());
            metaDataTemp.setEditionOrder(sCmfMeta.getEditionOrder());
            metaDataTemp.setEditionNote(sCmfMeta.getEditionNote());
            metaDataTemp.setPressOrder(sCmfMeta.getPressOrder());
            metaDataTemp.setContentNum(sCmfMeta.getContentNum());
            metaDataTemp.setClassCode(sCmfMeta.getClassCode());
            metaDataTemp.setLanguage(sCmfMeta.getLanguage());
            metaDataTemp.setApabiClass(sCmfMeta.getApabiClass());
            metaDataTemp.setIllustration(sCmfMeta.getIllustration());
            metaDataTemp.setReditor(sCmfMeta.getReditor());
            metaDataTemp.setNotes(sCmfMeta.getNotes());
            String isbn = sCmfMeta.getRealISBN();
            metaDataTemp.setIsbn(isbn);
            if (StringUtils.isNotBlank(isbn)) {
                isbn = isbn.replace("-", "");
                if (isbn.length() == 10) {
                    metaDataTemp.setIsbn10(isbn);
                } else if (isbn.length() == 13) {
                    metaDataTemp.setIsbn13(isbn);
                }
            }
            metaDataTemp.setPlace(sCmfMeta.getPlace());
            //默认值
            metaDataTemp.setHasCebx(0);
            metaDataTemp.setIsReadEpub(0);
            metaDataTemp.setIsReadCebxFlow(0);
            metaDataTemp.setCreateTime(new Date());
            return metaDataTemp;
        }
        return null;
    }

    //生成流式服务CmfDigitResfileSite
    public static CmfDigitResfileSite createCmfDigitResfileSite(SCmfDigitResfileSite sCmfDigitResfileSite) {
        if (sCmfDigitResfileSite != null) {
            CmfDigitResfileSite cmfDigitResfileSite = new CmfDigitResfileSite();
            cmfDigitResfileSite.setFileId(sCmfDigitResfileSite.getFileId());
            cmfDigitResfileSite.setSiteId(sCmfDigitResfileSite.getSiteId());
            cmfDigitResfileSite.setUrlFileName(sCmfDigitResfileSite.getUrlFileName());
            cmfDigitResfileSite.setUrlFilePath(sCmfDigitResfileSite.getUrlFilePath());
            return cmfDigitResfileSite;
        }
        return null;
    }

    //生成流式服务cmfDigitObject
    public static CmfDigitObject createCmfDigitObject(SCmfDigitObject sCmfDigitObject) {
        if (sCmfDigitObject != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CmfDigitObject cmfDigitObject = new CmfDigitObject();
            cmfDigitObject.setFileId(sCmfDigitObject.getFileId());
            cmfDigitObject.setDrId(sCmfDigitObject.getDrId());
            cmfDigitObject.setPfileId(sCmfDigitObject.getPfileId());
            cmfDigitObject.setFileName(sCmfDigitObject.getFileName());
            cmfDigitObject.setFileDesc(sCmfDigitObject.getFileDesc());

            cmfDigitObject.setFilePath(sCmfDigitObject.getFilePath());
            cmfDigitObject.setOrderNew(sCmfDigitObject.getOrder());
            cmfDigitObject.setObjId(sCmfDigitObject.getObjId());
            cmfDigitObject.setPobjId(sCmfDigitObject.getPobjId());
            cmfDigitObject.setDoi(sCmfDigitObject.getDoi());

            cmfDigitObject.setFileSize(sCmfDigitObject.getFileSize());
            cmfDigitObject.setFormatNew(sCmfDigitObject.getFormat());
            cmfDigitObject.setContentTableInfo(sCmfDigitObject.getContentTableInfo());
            cmfDigitObject.setEncryptInfo(sCmfDigitObject.getEncryptInfo());
            cmfDigitObject.setObjType(sCmfDigitObject.getObjType());

            cmfDigitObject.setImgWidth(sCmfDigitObject.getImgWidth());
            cmfDigitObject.setImgHeigth(sCmfDigitObject.getImgHeigth());
            cmfDigitObject.setChapterIndex(sCmfDigitObject.getChapterIndex());
            cmfDigitObject.setFileCreatedDate(sdf.format(sCmfDigitObject.getFileCreatedDate()));
            cmfDigitObject.setFileLastModDate(sdf.format(sCmfDigitObject.getFileLastModDate()));

            cmfDigitObject.setContentFormat(sCmfDigitObject.getContentFormat());
            cmfDigitObject.setCebxSubset(sCmfDigitObject.getCebxSubset());
            cmfDigitObject.setSecurityNew(sCmfDigitObject.getSecurity());
            //cmfDigitObject.setcatalog
            return cmfDigitObject;
        }
        return null;
    }

    //生成流式服务CmfMeta
    public static CmfMeta createCmfMeta(SCmfMeta sCmfMeta) {
        if (sCmfMeta != null) {
            CmfMeta cmfMeta = new CmfMeta();
            cmfMeta.setDrId(sCmfMeta.getDrid());
            cmfMeta.setTitle(sCmfMeta.getTitle());
            cmfMeta.setAlternativeTitle(sCmfMeta.getAlternativeTitle());
            cmfMeta.setCreator(sCmfMeta.getCreator());
            cmfMeta.setSubject(sCmfMeta.getSubject());

            cmfMeta.setAbstract(sCmfMeta.getAbstract());
            cmfMeta.setPublisher(sCmfMeta.getPublisher());
            cmfMeta.setType(sCmfMeta.getType());
            cmfMeta.setContributor(sCmfMeta.getContributor());
            cmfMeta.setIssuedDate(sCmfMeta.getIssuedDate());

            cmfMeta.setYear(sCmfMeta.getYear());
            cmfMeta.setIdType(sCmfMeta.getIdType());
            cmfMeta.setIdentifier(sCmfMeta.getIdentifier());
            cmfMeta.setRelation(sCmfMeta.getRelation());
            cmfMeta.setPrice(sCmfMeta.getPrice());

            cmfMeta.setPaperPrice(sCmfMeta.getPaperPrice());
            cmfMeta.setForeignPrice(sCmfMeta.getForeignPrice());
            cmfMeta.setForeignPriceType(sCmfMeta.getForeignPriceType());
            cmfMeta.setEditionOrder(sCmfMeta.getEditionOrder());
            cmfMeta.setEditionNote(sCmfMeta.getEditionNote());

            cmfMeta.setPressOrder(sCmfMeta.getPressOrder());
            cmfMeta.setContentNum(sCmfMeta.getContentNum());
            cmfMeta.setClassCode(sCmfMeta.getClassCode());
            cmfMeta.setLanguage(sCmfMeta.getLanguage());
            cmfMeta.setApabiClass(sCmfMeta.getApabiClass());

            cmfMeta.setIllustration(sCmfMeta.getIllustration());
            cmfMeta.setReditor(sCmfMeta.getReditor());
            cmfMeta.setNotes(sCmfMeta.getNotes());
            cmfMeta.setRealISBN(sCmfMeta.getRealISBN());
            cmfMeta.setPlace(sCmfMeta.getPlace());

            return cmfMeta;
        }
        return null;
    }

    //生成内容检查结果
    public static void exportExcel(List<BookChapterDetect> chapterDetects, String excelPath) {
        FileOutputStream fos;
        try {
            //Workbook book = new HSSFWorkbook();
            Workbook book = new SXSSFWorkbook();
            Sheet sheet = book.createSheet("sheet1");
            //日期格式
            /*HSSFCellStyle cellStyle = (HSSFCellStyle) book.createCellStyle();
            HSSFDataFormat format = (HSSFDataFormat) book.createDataFormat();
            cellStyle.setDataFormat(format.getFormat("yyyy-MM-dd"));*/
            // 在对应的Excel中建立一个分表
            for (int i = 0; i < chapterDetects.size(); i++) {
                Row row = sheet.createRow(i);

                // 在所在的行设置所在的单元格（相当于列，初始从0开始,对应的就是A列）
                Cell cell = row.createCell(0);
                Cell cell1 = row.createCell(1);
                Cell cell2 = row.createCell(2);
                Cell cell3 = row.createCell(3);
                Cell cell4 = row.createCell(4);

                cell.setCellValue(chapterDetects.get(i).getMetaId());
                cell1.setCellValue(chapterDetects.get(i).getTitle());
                cell2.setCellValue(chapterDetects.get(i).getChapterNum());
                cell3.setCellValue(chapterDetects.get(i).getChapterName());
                cell4.setCellValue(chapterDetects.get(i).getMessage());

            }
            fos = new FileOutputStream(excelPath);
            book.write(fos);
            fos.close();
        } catch (Exception e) {
            log.warn("流式内容检查结果生成异常{}", e.getMessage());
        }
    }

    //生成批量操作结果
    public static void exportExcelEmail(List<EmailResult> emailResults, String excelPath) {
        FileOutputStream fos;
        try {
            Workbook book = new SXSSFWorkbook();
            Sheet sheet = book.createSheet("sheet1");
            // 在对应的Excel中建立一个分表
            for (int i = 0; i < emailResults.size(); i++) {
                Row row = sheet.createRow(i);

                // 在所在的行设置所在的单元格（相当于列，初始从0开始,对应的就是A列）
                Cell cell = row.createCell(0);
                Cell cell1 = row.createCell(1);

                cell.setCellValue(emailResults.get(i).getId());
                cell1.setCellValue(emailResults.get(i).getMessage());

            }
            fos = new FileOutputStream(excelPath);
            book.write(fos);
            fos.close();
        } catch (Exception e) {
            log.warn("批量操作结果生成异常{}", e.getMessage());
        }
    }
    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 依据图片路径生成缩略图 </p>
     *
     * @param imagePath 原图片路径
     * @param w         缩略图宽
     * @param h         缩略图高
     * @param prevfix   生成缩略图的前缀
     * @param force     是否强制依照宽高生成缩略图(假设为false，则生成最佳比例缩略图)
     */
    public synchronized static void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if (imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀所有小写，然后推断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0) {
                    log.error("后缀名不合法. 合法的后缀名是 {}." + types);
                    return;
                }
                log.debug("缩略图宽:{}, 高:{}.", w, h);
                Image img = ImageIO.read(imgFile);
                if (!force) {
                    // 依据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if ((width * 1.0) / w < (height * 1.0) / h) {
                        if (width > w) {
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
                            log.debug("更改图片的高度, 宽为:{}, 高为:{}.", w, h);
                        }
                    } else {
                        if (height > h) {
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
                            log.debug("更改图片的宽度, 宽为:{}, 高为:{}.", w, h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                String p = imgFile.getPath();
                // 将图片保存在原文件夹并加上前缀
                ImageIO.write(bi, suffix, new File(p.substring(0, p.lastIndexOf(File.separator)) + File.separator + prevfix + "." + suffix));
                log.debug("缩略图在原路径下生成成功");
            } catch (IOException e) {
                log.error("生成缩略图失败.", e);
            }
        } else {
            log.warn("图片不存在.");
        }
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "毫秒");
    }
}
