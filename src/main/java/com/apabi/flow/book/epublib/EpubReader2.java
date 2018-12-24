package com.apabi.flow.book.epublib;

import net.sf.jazzlib.ZipFile;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.epub.NCXDocument;
import nl.siegmann.epublib.epub.PackageDocumentReader;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/12/24 9:14
 * @description
 */
public class EpubReader2 extends nl.siegmann.epublib.epub.EpubReader {
    private static final Logger log = LoggerFactory.getLogger(nl.siegmann.epublib.epub.EpubReader.class);
    private BookProcessor bookProcessor;

    public EpubReader2() {
        this.bookProcessor = BookProcessor.IDENTITY_BOOKPROCESSOR;
    }

    @Override
    public Book readEpub(InputStream in) throws IOException {
        return this.readEpub(in, "UTF-8");
    }

    @Override
    public Book readEpub(ZipInputStream in) throws IOException {
        return this.readEpub(in, "UTF-8");
    }

    @Override
    public Book readEpub(InputStream in, String encoding) throws IOException {
        return this.readEpub(new ZipInputStream(in), encoding);
    }

    @Override
    public Book readEpub(ZipInputStream in, String encoding) throws IOException {
        return this.readEpub(ResourcesLoader.loadResources(in, encoding));
    }

    @Override
    public Book readEpub(Resources resources) throws IOException {
        return this.readEpub(resources, new Book());
    }

    @Override
    public Book readEpub(Resources resources, Book result) throws IOException {
        if (result == null) {
            result = new Book();
        }

        this.handleMimeType(result, resources);
        String packageResourceHref = this.getPackageResourceHref(resources);
        Resource packageResource = this.processPackageResource(packageResourceHref, result, resources);
        result.setOpfResource(packageResource);
        Resource ncxResource = this.processNcxResource(packageResource, result);
        result.setNcxResource(ncxResource);
        result = this.postProcessBook(result);
        return result;
    }

    private Book postProcessBook(Book book) {
        if (this.bookProcessor != null) {
            book = this.bookProcessor.processBook(book);
        }

        return book;
    }

    private Resource processNcxResource(Resource packageResource, Book book) {
        return NCXDocument.read(book, this);
    }

    private Resource processPackageResource(String packageResourceHref, Book book, Resources resources) {
        Resource packageResource = resources.remove(packageResourceHref);

        try {
            PackageDocumentReader.read(packageResource, this, book, resources);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return packageResource;
    }

    private String getPackageResourceHref(Resources resources) {
        String defaultResult = "OEBPS/content.opf";
        String result = defaultResult;
        Resource containerResource = resources.remove("META-INF/container.xml");
        if (containerResource == null) {
            return defaultResult;
        } else {
            try {
                Document document = ResourceUtil.getAsDocument(containerResource);
                Element rootFileElement = (Element)((Element)document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
                result = rootFileElement.getAttribute("full-path");
            } catch (Exception var7) {
                log.error(var7.getMessage(), var7);
            }

            if (StringUtil.isBlank(result)) {
                result = defaultResult;
            }

            return result;
        }
    }

    private void handleMimeType(Book result, Resources resources) {
        resources.remove("mimetype");
    }
}
