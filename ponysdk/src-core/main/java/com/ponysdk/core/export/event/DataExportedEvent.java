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
package com.ponysdk.core.export.event;

import com.ponysdk.core.event.BusinessEvent;
import com.ponysdk.core.event.Event;
import com.ponysdk.core.export.ExportContext.ExportType;

public class DataExportedEvent extends BusinessEvent<DataExportedHandler> {

    public static final Event.Type<DataExportedHandler> TYPE = new Event.Type<DataExportedHandler>();

    private final ExportType exportType;

    public DataExportedEvent(Object sourceComponent, ExportType exportType) {
        super(sourceComponent);
        this.exportType = exportType;
    }

    @Override
    protected void dispatch(DataExportedHandler handler) {
        handler.onDataExported(this);
    }

    @Override
    public Event.Type<DataExportedHandler> getAssociatedType() {
        return TYPE;
    }

    public ExportType getExportType() {
        return exportType;
    }

}
