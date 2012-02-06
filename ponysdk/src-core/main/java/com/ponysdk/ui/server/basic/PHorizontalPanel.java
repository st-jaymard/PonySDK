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

package com.ponysdk.ui.server.basic;

import com.ponysdk.ui.terminal.PropertyKey;
import com.ponysdk.ui.terminal.WidgetType;
import com.ponysdk.ui.terminal.basic.PHorizontalAlignment;
import com.ponysdk.ui.terminal.basic.PVerticalAlignment;
import com.ponysdk.ui.terminal.instruction.Update;

public class PHorizontalPanel extends PCellPanel implements HasPAlignment {

    private PHorizontalAlignment horizontalAlignment = PHorizontalAlignment.ALIGN_LEFT;

    private PVerticalAlignment verticalAlignment = PVerticalAlignment.ALIGN_TOP;

    @Override
    protected WidgetType getType() {
        return WidgetType.HORIZONTAL_PANEL;
    }

    @Override
    public void setHorizontalAlignment(PHorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        final Update update = new Update(getID());
        update.setMainPropertyValue(PropertyKey.HORIZONTAL_ALIGNMENT, horizontalAlignment.ordinal());
        getPonySession().stackInstruction(update);
    }

    @Override
    public void setVerticalAlignment(PVerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        final Update update = new Update(getID());
        update.setMainPropertyValue(PropertyKey.VERTICAL_ALIGNMENT, verticalAlignment.ordinal());
        getPonySession().stackInstruction(update);
    }

    public PHorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public PVerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

}
