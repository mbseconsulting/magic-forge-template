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
package com.lumon.macrodatarenaming.configurator;

import java.util.List;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.ui.browser.Tree;

public class RenameBrowerContextAMConfigurator implements BrowserContextAMConfigurator {
    private final ActionsCategory renameCategory;

    /**
     * Constructs a RenameBrowerContextAMConfigurator with the specified renaming category.
     *
     * @param renameCategory The renaming category to add to the browser context menu.
     */
    public RenameBrowerContextAMConfigurator(ActionsCategory renameCategory) {
        this.renameCategory = renameCategory;
    }

    /**
     * Configures the ActionsManager to add the renaming category to the browser context menu.
     *
     * @param actionsManager The ActionsManager to configure.
     * @param tree The browser tree.
     */
    @Override
    public void configure(ActionsManager actionsManager, Tree tree) {
        // Get the "Create Element" category from the browser context menu
        ActionsCategory createElementCategory =
                (ActionsCategory)
                        actionsManager.getActionFor(ActionsID.NEW_ELEMENT_POPUP_CATEGORY_ID);

        if (createElementCategory != null) {
            // Add the renaming category to the "Create Element" category
            List<NMAction> actionsInCategory = createElementCategory.getActions();
            // Add renaming category
            actionsInCategory.add(renameCategory);
            // Update the actions in the "Create Element" category
            createElementCategory.setActions(actionsInCategory);
        }
    }

    @Override
    public int getPriority() {
        return LOW_PRIORITY;
    }
}
