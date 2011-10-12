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
package com.ponysdk.core.export;

import java.io.Serializable;
import java.util.List;

import com.ponysdk.core.query.Query;
import com.ponysdk.ui.server.list.SelectionResult;

public class ExportContext<T> implements Serializable {

    public enum ExportType {
        CSV("csv"), PDF("pdf"), XML("xml");
        private String extension;
        private static String DOT = ".";

        private ExportType(String extension) {
            this.extension = extension;
        }

        public String getFileName(String name) {
            return name + DOT + extension;
        }
    }

    private Query query;
    private List<ExportableField> exportableFields;
    private ExportType type = ExportType.CSV;
    private SelectionResult<T> selectionResult;

    public ExportContext() {
    }

    public ExportContext(Query query, List<ExportableField> exportableFields, SelectionResult<T> selectionResult) {
        this.query = query;
        this.exportableFields = exportableFields;
        this.selectionResult = selectionResult;
    }

    public Query getQuery() {
        return query;
    }

    public List<ExportableField> getExportableFields() {
        return exportableFields;
    }

    public ExportType getType() {
        return type;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setExportableFields(List<ExportableField> exportableFields) {
        this.exportableFields = exportableFields;
    }

    public void setType(ExportType type) {
        this.type = type;
    }

    public SelectionResult<T> getSelectionResult() {
        return selectionResult;
    }
}
