package com.apabi.flow.book.util;

import com.apabi.flow.book.model.BookCataRows;
import com.apabi.flow.book.model.BookMeta;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author guanpp
 * @date 2018/7/2 16:27
 * @description
 */
public class Xml2BookMeta {

    private static String LIB_ID = "bjsw";
    private static String ROUTE = "shard1";

    private static Map<String, String> codeMap;

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
    public static BookMeta docToBookMeta(Document doc) throws ParseException {
        if (doc != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            BookMeta bookMeta = new BookMeta();
            //获取目录数据
            //List<String> cataRows = new ArrayList<>();

            List<Element> cataList = (List<Element>) doc.selectNodes("//Catalog/catalogRow");
            if (cataList != null && cataList.size() > 0) {
                BookCataRows root = new BookCataRows();
                for (Element element : cataList) {
                    getCataTree(element, root);
                }
                JSONArray json = JSONArray.fromObject(root.getChildren());
                bookMeta.setFoamatCatalog(json.toString());
            }
            //获取file数据
            List<String> files = new ArrayList<>();
            List<Element> fileList = (List<Element>) doc.selectNodes("//fileSec/file");
            if (fileList != null && fileList.size() > 0) {
                String file;
                String format;
                String objID;
                String fileSize;
                for (Element element : fileList) {
                    format = element.element("techMD").element("Format").getText();
                    objID = element.element("id").element("ObjID").getText();
                    fileSize = element.element("techMD").element("FileSize").getText();
                    file = "{\"fileName\":\"" + element.attributeValue("FileName") + "\"," +
                            "\"fileSize\":\"" + fileSize + "\"," +
                            "\"filePath\":\"" + element.attributeValue("FilePath") + "\"," +
                            "\"objID\":\"" + objID + "\"," +
                            "\"format\":\"" + format + "\"," +
                            "\"order\":\"" + element.attributeValue("Order") + "\"}";
                    files.add(file);
                    if (format.toLowerCase().equals("cebx")) {
                        bookMeta.setCebxObjId(objID);
                        bookMeta.setCebxFileSize(fileSize);
                        bookMeta.setCebxPage(element.element("techMD").element("Page").getText());
                    } else if (format.toLowerCase().equals("jpg") || format.toLowerCase().equals("pmg")) {
                        bookMeta.setCovObjId(objID);
                        bookMeta.setThumImgSize(fileSize);
                        bookMeta.setImgWidth(element.element("techMD").element("ImgWidth").getText());
                        bookMeta.setImgHeigth(element.element("techMD").element("ImgHeigth").getText());
                    }
                }
            }
            //bookMeta.setFile(files);
            //获取libId，暂时给默认值bjsw
            bookMeta.setLibraryId(BookConstant.LIBID);
            //获取categoryId，暂时存放ClassCode
            //List<String> categoryIds = new ArrayList<>();
            String categoryIds = "";
            List<Element> categoryIdList = (List<Element>) doc.selectNodes("//ClassCode");
            if (categoryIdList != null && categoryIdList.size() > 0) {
                String categoryId;
                for (Element element : categoryIdList) {
                    categoryId = element.getText();
                    categoryIds += categoryId + ",";
                    //categoryIds.add(categoryId);
                }
            }
            bookMeta.setDepartmentId(categoryIds);
            //获取图书meta数据
            bookMeta.setMetaId(doc.selectSingleNode("//identifier").getText());
            bookMeta.setComplexOid(doc.selectSingleNode("//complexOID").getText());
            bookMeta.setCreateTime(sdf.parse(doc.selectSingleNode("//createdDate").getText()));
            bookMeta.setUpdateTime(sdf.parse(doc.selectSingleNode("//lastModifiedDate").getText()));
            bookMeta.setIssuedDate(doc.selectSingleNode("//IssuedDate").getText());
            bookMeta.setTitle(doc.selectSingleNode("//Title").getText());
            bookMeta.setAlternativeTitle(doc.selectSingleNode("//AlternativeTitle").getText());
            bookMeta.setCreator(doc.selectSingleNode("//Creator").getText());
            //bookMeta.setCreatorType(((Element) doc.selectSingleNode("//Creator")).attributeValue("CreatorType"));
            bookMeta.setAbstract_(doc.selectSingleNode("//Abstract").getText());
            bookMeta.setSubject(doc.selectSingleNode("//Subject").getText());
            bookMeta.setPublisher(doc.selectSingleNode("//Publisher").getText());
            bookMeta.setPlace(((Element) doc.selectSingleNode("//Publisher")).attributeValue("Place"));
            bookMeta.setContributor(doc.selectSingleNode("//Contributor").getText());
            bookMeta.setIsbn(doc.selectSingleNode("//RealISBN").getText());
            Node isSeries = doc.selectSingleNode("//IsSeries");
            if (isSeries != null && !StringUtils.isEmpty(isSeries.getText())) {
                bookMeta.setIsSeries(Integer.parseInt(isSeries.getText()));
            } else {
                bookMeta.setIsSeries(0);
            }

            Node volumesCount = doc.selectSingleNode("//VolumesCount");
            if (volumesCount != null) {
                bookMeta.setVolumesCount(volumesCount.getText());
            } else {
                bookMeta.setVolumesCount("0");
            }
            bookMeta.setEbookPrice(doc.selectSingleNode("//Price").getText());
            bookMeta.setPaperPrice(doc.selectSingleNode("//PaperPrice").getText());
            bookMeta.setForeignPrice(doc.selectSingleNode("//ForeignPrice").getText());
            bookMeta.setEditionOrder(doc.selectSingleNode("//EditionOrder").getText());
            bookMeta.setEditionNote(doc.selectSingleNode("//EditionNote").getText());
            bookMeta.setPressOrder(doc.selectSingleNode("//PressOrder").getText());
            String contentNum = doc.selectSingleNode("//ContentNum").getText();
            if (!StringUtils.isEmpty(contentNum)) {
                bookMeta.setContentNum(Integer.parseInt(contentNum));
            }
            bookMeta.setClassCode(doc.selectSingleNode("//ClassCode").getText());
            bookMeta.setApabiClass(doc.selectSingleNode("//ApabiClass").getText());
            bookMeta.setIllustration(doc.selectSingleNode("//Illustration").getText());
            bookMeta.setReditor(doc.selectSingleNode("//Reditor").getText());
            bookMeta.setNotes(doc.selectSingleNode("//Notes").getText());
            bookMeta.setType(doc.selectSingleNode("//Type").getText());
            bookMeta.setLanguage(doc.selectSingleNode("//Language").getText());
            return bookMeta;
        }
        return null;
    }

    //构建目录树
    //构建目录对象list
    private static void getCataTree(Element element, BookCataRows parentCata) {
        if (element != null) {
            List<Element> childE = element.elements();
            if (childE != null && childE.size() > 0) {
                BookCataRows cataRows = new BookCataRows();
                String chapterName = element.attributeValue("chapterName");
                cataRows.setChapterName(chapterName);
                cataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("ebookPageNum")));
                for (Element child : childE) {
                    getCataTree(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                String chapterName = element.attributeValue("chapterName");
                bookCataRows.setChapterName(chapterName);
                bookCataRows.setEbookPageNum(Integer.parseInt(element.attributeValue("ebookPageNum")));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //获取xml文件中的id
    public static String getMetaId4Xml(String path) throws Exception {
        if (!StringUtils.isEmpty(path)) {
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            if (suffix.toLowerCase().equals("xml")) {
                SAXReader saxReader = new SAXReader();
                Document doc = saxReader.read(new File(path));
                Element root = doc.getRootElement();
                String metaId = root.element("header").elementTextTrim("identifier");
                return metaId;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Document document = getXmlSourceFromUrl("C:\\Users\\guanpp\\Desktop\\xml\\m(2E)20130812-WXNX-889-0186.xml");
        //docToBookMeta(document);
        System.out.println();
    }
}
