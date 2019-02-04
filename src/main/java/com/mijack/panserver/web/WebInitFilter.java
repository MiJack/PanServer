/*
 * Copyright 2019 Mi&Jack
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mijack.panserver.web;

import com.mijack.panserver.event.WebInitEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mi&Jack
 */
public class WebInitFilter extends GenericFilterBean implements ApplicationListener<WebInitEvent> {

    public static final String UNINITIALIZED_URL = "/0/uninitialized.html";
    public static final String INITIALIZATION_FAILED_URL = "/0/initialization/failed.html";
    private WebInitEvent.WebStatus webStatus = WebInitEvent.WebStatus.UNINITIALIZED;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.equals(UNINITIALIZED_URL) || requestURI.equals(INITIALIZATION_FAILED_URL)) {
            chain.doFilter(request, response);
            return;
        }
        switch (webStatus) {
            case UNINITIALIZED:
                ((HttpServletResponse) response).sendRedirect(UNINITIALIZED_URL);
                return;
            case INITIALIZED:
                chain.doFilter(request, response);
                return;
            case INITIALIZATION_FAILED:
            default:
                logger.debug("未知异常");
                ((HttpServletResponse) response).sendRedirect(INITIALIZATION_FAILED_URL);
                return;
        }

    }

    @Override
    public void onApplicationEvent(WebInitEvent event) {
        webStatus = event.getWebStatus();
    }
}
