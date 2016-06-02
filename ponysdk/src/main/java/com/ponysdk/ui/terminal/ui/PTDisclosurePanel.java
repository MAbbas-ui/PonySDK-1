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

package com.ponysdk.ui.terminal.ui;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.ponysdk.ui.model.ClientToServerModel;
import com.ponysdk.ui.model.ServerToClientModel;
import com.ponysdk.ui.terminal.UIBuilder;
import com.ponysdk.ui.terminal.instruction.PTInstruction;
import com.ponysdk.ui.terminal.model.BinaryModel;
import com.ponysdk.ui.terminal.model.ReaderBuffer;

public class PTDisclosurePanel extends PTWidget<DisclosurePanel> {
    private static final String OPENNED = "images/disclosure_openned.png";
    private static final String CLOSED = "images/disclosure_closed.png";

    private String headerText;
    private PImageResource openImageResource;
    private PImageResource closeImageResource;

    @Override
    public void create(final ReaderBuffer buffer, final int objectId, final UIBuilder uiService) {
        headerText = buffer.getBinaryModel().getStringValue();

        // TODO nciaravola pass url + size in parameters
        openImageResource = new PImageResource(OPENNED, 0, 0, 14, 14);
        closeImageResource = new PImageResource(CLOSED, 0, 0, 14, 14);

        super.create(buffer, objectId, uiService);

        addHandlers(buffer, uiService);
    }

    @Override
    protected DisclosurePanel createUIObject() {
        return new DisclosurePanel(openImageResource, closeImageResource, headerText);
    }

    private void addHandlers(final ReaderBuffer buffer, final UIBuilder uiService) {
        uiObject.addCloseHandler(new CloseHandler<DisclosurePanel>() {

            @Override
            public void onClose(final CloseEvent<DisclosurePanel> event) {
                final PTInstruction instruction = new PTInstruction(getObjectID());
                instruction.put(ClientToServerModel.HANDLER_CLOSE);
                uiService.sendDataToServer(uiObject, instruction);
            }
        });

        uiObject.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(final OpenEvent<DisclosurePanel> event) {
                final PTInstruction instruction = new PTInstruction(getObjectID());
                instruction.put(ClientToServerModel.HANDLER_OPEN);
                uiService.sendDataToServer(uiObject, instruction);
            }
        });
    }

    @Override
    public void add(final ReaderBuffer buffer, final PTObject ptObject) {
        uiObject.setContent(asWidget(ptObject));
    }

    @Override
    public boolean update(final ReaderBuffer buffer, final BinaryModel binaryModel) {
        if (ServerToClientModel.OPEN.equals(binaryModel.getModel())) {
            uiObject.setOpen(binaryModel.getBooleanValue());
            return true;
        }
        if (ServerToClientModel.ANIMATION.equals(binaryModel.getModel())) {
            uiObject.setAnimationEnabled(binaryModel.getBooleanValue());
            return true;
        }
        return super.update(buffer, binaryModel);
    }

}