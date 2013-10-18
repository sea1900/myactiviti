package com.ces.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.ces.common.webbean.Page;

/**
 * 分页工具
 * 
 * @author hc
 */
public class PageUtil {

  public static int PAGE_SIZE = 15;

  public static int[] init(Page<?> page, HttpServletRequest request) {
    int pageNumber = Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("p"), "1"));
    page.setPageNo(pageNumber);
    int pageSize = Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("ps"), String.valueOf(PAGE_SIZE)));
    page.setPageSize(pageSize);
    int firstResult = page.getFirst() - 1;
    int maxResults = page.getPageSize();
    return new int[] {firstResult, maxResults};
  }

}
