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
package com.ponysdk.core.main;

import java.io.IOException;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

public class Main {

    protected static final Logger log = LoggerFactory.getLogger(Main.class);

    private Server webServer;
    private String configurationFile;
    private Integer port;
    private String war;
    private String applicationContextName;

    public static void main(final String[] args) throws Exception {
        final Main main = new Main();

        for (final String arg : args) {
            final String[] parameter = arg.split("=");

            if (parameter[0].equals("contextName")) {
                main.setApplicationContextName(parameter[1]);
            } else if (parameter[0].equals("configurationFile")) {
                main.setConfigurationFile(parameter[1]);
            } else if (parameter[0].equals("port")) {
                main.setPort(Integer.valueOf(parameter[1]));
            } else if (parameter[0].equals("war")) {
                main.setWar(parameter[1]);
            }
        }

        main.start();
    }

    public void start() throws Exception {
        final GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        if (configurationFile != null) {
            ctx.load("classpath:" + configurationFile);
            ctx.refresh();
        }

        if (port != null) {
            webServer = new Server(port);
        } else {
            webServer = new Server();
        }

        addWebApplication();
        webServer.start();
    }

    private void addWebApplication() throws IOException {
        final WebAppContext webapp = new WebAppContext();
        if (applicationContextName != null) {
            webapp.setContextPath("/" + applicationContextName);
            webapp.setDescriptor(applicationContextName);
        } else {
            webapp.setContextPath("/");
        }

        webapp.setWar(war);

        webapp.setExtractWAR(true);
        webapp.setParentLoaderPriority(true);
        webapp.setClassLoader(new WebAppClassLoader(Main.class.getClassLoader(), webapp));

        webServer.addHandler(webapp);
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setApplicationContextName(String applicationContextName) {
        this.applicationContextName = applicationContextName;
    }

    public void setWar(String war) {
        this.war = war;
    }
}
