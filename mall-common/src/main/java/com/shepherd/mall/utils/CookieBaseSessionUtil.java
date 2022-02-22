package com.shepherd.mall.utils;


import com.shepherd.mall.base.CasProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/7/14 19:23
 */

public class CookieBaseSessionUtil {

    private CasProperties casProperties;

    public CasProperties getCasProperties() {
        return casProperties;
    }

    public void setCasProperties(CasProperties casProperties) {
        this.casProperties = casProperties;
    }

    public String getRequestedSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }

            if (!casProperties.getCookieName().equalsIgnoreCase(cookie.getName())) {
                continue;
            }

            return cookie.getValue();
        }
        return null;
    }

    public void onNewSession(HttpServletRequest request,
                             HttpServletResponse response) {
        String sessionId = (String) request.getAttribute(casProperties.getCookieName());
        Cookie cookie = new Cookie(casProperties.getCookieName(), sessionId);
//        cookie.setDomain(request.getRemoteHost());
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath() + "/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }

}
