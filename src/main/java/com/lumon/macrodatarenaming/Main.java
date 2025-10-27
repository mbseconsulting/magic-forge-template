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
package com.lumon.macrodatarenaming;

import java.util.Set;
import java.util.UUID;

import com.lumon.macrodatarenaming.action.RenameAction;
import com.lumon.macrodatarenaming.configurator.RenameBrowerContextAMConfigurator;
import com.lumon.macrodatarenaming.util.StringUtil;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.plugins.Plugin;

public class Main extends Plugin {

    @Override
    public void init() {
        Set<StringUtil.RenamingType> renamingTypes =
                Set.of(
                        StringUtil.RenamingType.PASCAL_CASE,
                        StringUtil.RenamingType.CAMEL_CASE,
                        StringUtil.RenamingType.SNAKE_CASE,
                        StringUtil.RenamingType.KEBAB_CASE,
                        StringUtil.RenamingType.SCREAMING_SNAKE_CASE,
                        StringUtil.RenamingType.REVERSE_NAME,
                        StringUtil.RenamingType.TITLE_CASE);

        // Create Renaming Menu
        ActionsCategory renameCategory =
                new ActionsCategory(UUID.randomUUID().toString(), "Renaming");
        renameCategory.setNested(true);
        // Add Actions to Renaming Menu
        for (StringUtil.RenamingType type : renamingTypes) {
            RenameAction renameAction =
                    new RenameAction("Rename to " + type.name().replace("_", " "), type);
            renameCategory.addAction(renameAction);
        }

        // Create Browser Context Configurator for Renaming Menu
        RenameBrowerContextAMConfigurator renameBrowerContextAMConfigurator =
                new RenameBrowerContextAMConfigurator(renameCategory);
        // Register the Renaming Menu in the Browser Context Menu
        ActionsConfiguratorsManager.getInstance()
                .addContainmentBrowserContextConfigurator(renameBrowerContextAMConfigurator);
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
