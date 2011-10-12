/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
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
package com.ponysdk.impl.webapplication.page;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ponysdk.core.security.SecurityManager;

public class DefaultPageProvider implements PageProvider, ApplicationContextAware {

    private final static Logger log = LoggerFactory.getLogger(DefaultPageProvider.class);

    private final Map<String, PageActivity> allPageActivitiesDeclared = new LinkedHashMap<String, PageActivity>();
    private Map<String, PageActivity> allActivePageActivities;

    @Override
    public PageActivity getPageActivity(String pageName) {
        if (allActivePageActivities == null) {
            initPagePermissions();
        }
        return allActivePageActivities.get(pageName);
    }

    @Override
    public Collection<PageActivity> getPageActivities() {
        if (allActivePageActivities == null) {
            initPagePermissions();
        }
        return allActivePageActivities.values();
    }

    private void initPagePermissions() {
        allActivePageActivities = new LinkedHashMap<String, PageActivity>();
        for (final Entry<String, PageActivity> entry : allPageActivitiesDeclared.entrySet()) {
            if (SecurityManager.checkPermission(entry.getValue().getPermission())) {
                allActivePageActivities.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, PageActivity> pageActivities = applicationContext.getBeansOfType(PageActivity.class);

        if (pageActivities.isEmpty()) {
            // throw new FatalBeanException("The application must contain at least 1 PageActivity");
            log.warn("The application doesn't have any page activity");
        }
        for (final PageActivity pageActivity : pageActivities.values()) {
            if (pageActivity.getPageName() != null) {
                allPageActivitiesDeclared.put(pageActivity.getPageName(), pageActivity);
            } else if (pageActivity.getPageCategory() != null) {
                allPageActivitiesDeclared.put(pageActivity.getPageCategory(), pageActivity);
            }
        }
    }
}
