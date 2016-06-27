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

package com.ponysdk.core.terminal.socket;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ponysdk.core.model.ClientToServerModel;
import com.ponysdk.core.model.ServerToClientModel;
import com.ponysdk.core.terminal.UIBuilder;
import com.ponysdk.core.terminal.model.BinaryModel;
import com.ponysdk.core.terminal.model.ReaderBuffer;
import com.ponysdk.core.terminal.request.WebSocketRequestBuilder;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.ArrayBuffer;
import elemental.html.WebSocket;
import elemental.html.Window;

public class WebSocketClient {

    private static final Logger log = Logger.getLogger(WebSocketClient.class.getName());

    private static final String ARRAYBUFFER_TYPE = "arraybuffer";

    private final WebSocket webSocket;

    public WebSocketClient(final String url, final UIBuilder uiBuilder, final int applicationViewID) {
        final Window window = Browser.getWindow();
        webSocket = window.newWebSocket(url);

        webSocket.setOnopen(new EventListener() {

            @Override
            public void handleEvent(final Event event) {
                if (log.isLoggable(Level.INFO)) log.info("WebSoket connected");
                uiBuilder.init(applicationViewID, new WebSocketRequestBuilder(WebSocketClient.this));
            }
        });
        webSocket.setOnclose(new EventListener() {

            @Override
            public void handleEvent(final Event event) {
                if (log.isLoggable(Level.INFO)) log.info("WebSoket disconnected");
                uiBuilder.onCommunicationError(new Exception("Websocket connection lost."));
            }
        });
        //webSocket.setOnerror(this);
        webSocket.setOnmessage(new EventListener() {

            /**
             * Message from server to Main terminal
             */
            @Override
            public void handleEvent(final Event event) {
                final ArrayBuffer arrayBuffer = (ArrayBuffer) ((MessageEvent) event).getData();
                try {
                    final ReaderBuffer buffer = new ReaderBuffer(arrayBuffer);
                    // Get the first element on the message, always a key of
                    // element of the Model enum
                    final BinaryModel type = buffer.readBinaryModel();

                    if (type.getModel() == ServerToClientModel.HEARTBEAT) {
                        if (log.isLoggable(Level.FINE)) log.log(Level.FINE, "Heart beat");
                        send(ClientToServerModel.HEARTBEAT.toStringValue());
                    } else if (type.getModel() == ServerToClientModel.APPLICATION_SEQ_NUM) {
                        try {
                            uiBuilder.updateMainTerminal(buffer);
                        } catch (final Exception e) {
                            log.log(Level.SEVERE, "Error while processing the " + buffer, e);
                        }
                    } else {
                        log.severe("Unknown model : " + type.getModel());
                    }
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Cannot parse " + arrayBuffer, e);
                }
            }
        });
        webSocket.setBinaryType(ARRAYBUFFER_TYPE);
    }

    public void send(final String message) {
        webSocket.send(message);
    }

    public void close() {
        webSocket.close();
    }

}
