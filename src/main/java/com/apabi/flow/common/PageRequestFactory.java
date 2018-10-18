package com.apabi.flow.common;

import cn.org.rapid_framework.page.PageRequest;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/3 10:32
 * @description
 */
public class PageRequestFactory {

    public static final int MAX_PAGE_SIZE = 1000;

    public static PageRequest bindPageRequest(PageRequest pageRequest,
                                              HttpServletRequest request) {
        return bindPageRequest(pageRequest, request, null);
    }

    public static PageRequest bindPageRequest(PageRequest pageRequest,
                                              HttpServletRequest request, String defaultSortColumns) {
        return bindPageRequest(pageRequest, request, defaultSortColumns,
                BaseQuery.DEFAULT_PAGE_SIZE);
    }

    /**
     * 绑定PageRequest的属性值
     */
    public static PageRequest bindPageRequest(PageRequest pageRequest,
                                              HttpServletRequest request, String defaultSortColumns,
                                              int defaultPageSize) {

        Map params = WebUtils.getParametersStartingWith(request, "s_");
        Iterator iterator = params.keySet().iterator();
        while(iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) params.get(key);
            if(value == null || value.trim().isEmpty()){
                iterator.remove();
            }
        }
        pageRequest.setFilters(params);

        pageRequest.setPageNumber(ServletRequestUtils.getIntParameter(request,
                "pageNumber", 1));
        pageRequest.setPageSize(ServletRequestUtils.getIntParameter(request,
                "pageSize", defaultPageSize));
        pageRequest.setSortColumns(ServletRequestUtils.getStringParameter(
                request, "sortColumns", defaultSortColumns));

        if (pageRequest.getPageSize() > MAX_PAGE_SIZE) {
            pageRequest.setPageSize(MAX_PAGE_SIZE);
        }
        return pageRequest;
    }
}
