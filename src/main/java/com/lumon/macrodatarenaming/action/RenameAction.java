/*
 * macrodata-renaming-license
 * Copyright © 2025 Mark S
 * Contact information: mark.s@lumon.com / https://www.lumon.com
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
package com.lumon.macrodatarenaming.action;

import java.awt.event.ActionEvent;
import java.util.UUID;

import com.lumon.macrodatarenaming.service.RenameService;
import com.lumon.macrodatarenaming.util.StringUtil;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/** Action that triggers the renaming operation on the selected element. */
public class RenameAction extends DefaultBrowserAction {
    private final StringUtil.RenamingType type;
    private final Element element;

    /**
     * Constructs a RenameAction with the specified name and renaming type.
     *
     * @param name The name of the action.
     * @param type The type of renaming to perform.
     */
    public RenameAction(String name, StringUtil.RenamingType type) {
        super(UUID.randomUUID().toString(), name, null, null);
        this.type = type;
        this.element = null;
    }

    /**
     * Constructs a RenameAction with a pre-selected element for testing. This constructor bypasses
     * browser tree selection, allowing direct element specification. Used primarily for integration
     * testing to achieve proper code coverage.
     *
     * @param name The name of the action.
     * @param type The type of renaming to perform.
     * @param element The element to rename (bypasses tree selection).
     */
    public RenameAction(String name, StringUtil.RenamingType type, Element element) {
        super(UUID.randomUUID().toString(), name, null, null);
        this.type = type;
        this.element = element;
    }

    /**
     * Invoked when the action is performed. It retrieves the selected element from the browser tree
     * and initiates the renaming operation.
     *
     * @param actionEvent The action event.
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Tree tree = getTree();
            Node selectedNode = tree != null ? tree.getSelectedNode() : null;
            Element element =
                    selectedNode != null ? (Element) selectedNode.getUserObject() : this.element;
            RenameService renameService = new RenameService(type, element);
            SessionManager.getInstance()
                    .callInsideSession(
                            Application.getInstance().getProject(), this.getName(), renameService);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
