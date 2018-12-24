package com.apabi.flow.book.epublib;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipException;
import net.sf.jazzlib.ZipFile;
import net.sf.jazzlib.ZipInputStream;
import nl.siegmann.epublib.domain.LazyResource;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.CollectionUtil;
import nl.siegmann.epublib.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/12/24 9:17
 * @description
 */
public class ResourcesLoader {


    private static final Logger LOG = LoggerFactory.getLogger(nl.siegmann.epublib.epub.ResourcesLoader.class);
    private static final ZipEntry ERROR_ZIP_ENTRY = new ZipEntry("<error>");

    public ResourcesLoader() {
    }

    public static Resources loadResources(ZipFile zipFile, String defaultHtmlEncoding, List<MediaType> lazyLoadedTypes) throws IOException {
        Resources result = new Resources();
        Enumeration entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (zipEntry != null && !zipEntry.isDirectory()) {
                String href = zipEntry.getName();
                Object resource;
                if (shouldLoadLazy(href, lazyLoadedTypes)) {
                    resource = new LazyResource(zipFile.getName(), zipEntry.getSize(), href);
                } else {
                    resource = ResourceUtil.createResource(zipEntry, zipFile.getInputStream(zipEntry));
                }

                if (((Resource) resource).getMediaType() == MediatypeService.XHTML) {
                    ((Resource) resource).setInputEncoding(defaultHtmlEncoding);
                }

                result.add((Resource) resource);
            }
        }

        return result;
    }

    private static boolean shouldLoadLazy(String href, Collection<MediaType> lazilyLoadedMediaTypes) {
        if (CollectionUtil.isEmpty(lazilyLoadedMediaTypes)) {
            return false;
        } else {
            MediaType mediaType = MediatypeService.determineMediaType(href);
            return lazilyLoadedMediaTypes.contains(mediaType);
        }
    }

    public static Resources loadResources(InputStream in, String defaultHtmlEncoding) throws IOException {
        return loadResources(new ZipInputStream(in), defaultHtmlEncoding);
    }

    public static Resources loadResources(ZipInputStream zipInputStream, String defaultHtmlEncoding) throws IOException {
        Resources result = new Resources();

        ZipEntry zipEntry = ERROR_ZIP_ENTRY;
        int errorNum = 0;
        do {
            try {
                //如果该文件错误数较多，则认为该文件异常
                if (errorNum > 22) {
                    return new Resources();
                }
                zipEntry = getNextZipEntry(zipInputStream);
            } catch (ZipException var3) {
                errorNum++;
                LOG.error(var3.getMessage());
                zipInputStream.closeEntry();
            }
            if (zipEntry != null && zipEntry != ERROR_ZIP_ENTRY && !zipEntry.isDirectory()) {
                Resource resource = ResourceUtil.createResource(zipEntry, zipInputStream);
                if (resource.getMediaType() == MediatypeService.XHTML) {
                    resource.setInputEncoding(defaultHtmlEncoding);
                }

                result.add(resource);
            }
        } while (zipEntry != null);

        return result;
    }

    private static ZipEntry getNextZipEntry(ZipInputStream zipInputStream) throws IOException {
        ZipEntry result = zipInputStream.getNextEntry();

        /*try {
        result = zipInputStream.getNextEntry();
        } catch (ZipException var3) {
            LOG.error(var3.getMessage());
            zipInputStream.closeEntry();
        }*/

        return result;
    }

    public static Resources loadResources(ZipFile zipFile, String defaultHtmlEncoding) throws IOException {
        return loadResources(zipFile, defaultHtmlEncoding, Collections.emptyList());
    }
}
