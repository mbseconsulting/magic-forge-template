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
package com.lumon.macrodatarenaming.service;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.lumon.macrodatarenaming.util.StringUtil;
import com.nomagic.magicdraw.uml.ClassTypes;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

/** Executes the renaming operation on the selected element and its sub-elements. */
public class RenameService implements Callable<Boolean> {
    private final StringUtil.RenamingType type;
    private final Element contextElement;

    /**
     * Constructs a RenameService with the specified renaming type and context element.
     *
     * @param type The type of renaming to perform.
     * @param contextElement The element to rename and its sub-elements.
     */
    public RenameService(StringUtil.RenamingType type, Element contextElement) {
        this.type = type;
        this.contextElement = contextElement;
    }

    /**
     * Renames the selected element and all its sub-elements based on the specified renaming type.
     *
     * @return true if the renaming operation was successful.
     */
    @Override
    public Boolean call() {
        // filter isEditable
        Collection<NamedElement> namedElementSet =
                Finder.byTypeRecursively()
                        .find(contextElement, ClassTypes.getSubtypesArray(NamedElement.class), true)
                        .stream()
                        .map(e -> (NamedElement) e)
                        .filter(NamedElement::isEditable)
                        .collect(Collectors.toList());

        namedElementSet.forEach(
                namedElement -> {
                    String originalName = namedElement.getName();
                    String newName;
                    switch (type) {
                        case PASCAL_CASE:
                            newName = StringUtil.toPascal(originalName);
                            break;
                        case CAMEL_CASE:
                            newName = StringUtil.toCamel(originalName);
                            break;
                        case SNAKE_CASE:
                            newName = StringUtil.toSnake(originalName);
                            break;
                        case KEBAB_CASE:
                            newName = StringUtil.toKebab(originalName);
                            break;
                        case SCREAMING_SNAKE_CASE:
                            newName = StringUtil.toScreamingSnake(originalName);
                            break;
                        case REVERSE_NAME:
                            newName = StringUtil.reverse(originalName);
                            break;
                        case TITLE_CASE:
                            newName = StringUtil.toTitleCase(originalName);
                            break;
                        default:
                            newName = originalName;
                            break;
                    }
                    namedElement.setName(newName);
                });
        return true;
    }
}
