/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *  Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *  Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *  
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ponysdk.core.service;

import java.util.Calendar;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponysdk.core.PonyApplicationSession;
import com.ponysdk.core.spring.ContextLoader;

public class ApplicationLoader implements ServletContextListener, HttpSessionListener {

    private static final Logger log = LoggerFactory.getLogger(ApplicationLoader.class);

    private final ContextLoader contextLoader;

    private String applicationName;
    private String applicationDescription;

    public ApplicationLoader() {
        contextLoader = new ContextLoader();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        applicationName = event.getServletContext().getInitParameter("applicationName");
        applicationDescription = event.getServletContext().getInitParameter("applicationDescription");

        printLicence();

        final String files = event.getServletContext().getInitParameter("contextConfigLocation");
        if (files != null && !files.isEmpty()) {
            contextLoader.initWebApplicationContext(event.getServletContext());
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        log.info("================================================");
        log.info("     " + applicationName.toLowerCase() + " - Context Destroyed                ");
        log.info("================================================");

        if (contextLoader != null) {
            contextLoader.closeWebApplicationContext(event.getServletContext());
        }
    }

    private void printLicence() {
        log.info("===============================================");
        log.info("     " + applicationName + " - " + applicationDescription + "            ");
        log.info("             WEB APPLICATION                   ");
        log.info("     (c) " + Calendar.getInstance().get(Calendar.YEAR) + " PonySDK         ");
        log.info("                                               ");
        log.info("===============================================");
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        if (log.isDebugEnabled()) {
            log.debug("Session created #" + arg0.getSession().getId());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        final PonyApplicationSession applicationSession = (PonyApplicationSession) arg0.getSession().getAttribute(PonyApplicationSession.class.getCanonicalName());
        applicationSession.fireSessionDestroyed(arg0);
    }

}
