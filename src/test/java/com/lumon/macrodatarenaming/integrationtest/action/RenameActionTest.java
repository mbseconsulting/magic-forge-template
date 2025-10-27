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
package com.lumon.macrodatarenaming.integrationtest.action;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;
import java.io.File;

import com.lumon.macrodatarenaming.action.RenameAction;
import com.lumon.macrodatarenaming.util.StringUtil;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.tests.MagicDrawApplication;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests for RenameAction using JUnit 5 with MagicDraw application. Tests UI action
 * triggers and session management.
 */
@Tag("IntegrationTest")
@ExtendWith(MagicDrawApplication.class)
@DisplayName("RenameAction Integration Tests")
class RenameActionTest {

    private static Project project;
    private Package model;
    private Package testPackage;

    @BeforeAll
    static void setUpAll() {
        project = Application.getInstance().getProjectsManager().createProject();
        assertNotNull(project, "Project should be created by MagicDrawApplication");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        model = project.getPrimaryModel();
        assertNotNull(model, "Primary model should exist");

        String currentTestName = testInfo.getDisplayName().replace(" ", "_");
        testPackage = createPackage(model, currentTestName);
        assertNotNull(testPackage, "Test package should be created");
    }

    @AfterAll
    static void tearDown() {
        try {
            String fileName =
                    String.format(
                            "build/test-results/integrationTest/mdzips/RenameActionTest.mdzip");
            File projectFile = new File(fileName);
            projectFile.getParentFile().mkdirs();

            // Create descriptor for the target file location
            ProjectDescriptor descriptor =
                    ProjectDescriptorsFactory.createLocalProjectDescriptor(project, projectFile);

            // Save project using ProjectsManager
            Application.getInstance().getProjectsManager().saveProject(descriptor, true);
        } catch (Exception e) {
            System.err.println("Failed to save test project: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Action execution with valid selection renames element")
    void testActionExecutionWithValidSelection() {
        Class testClass = createClass(testPackage, "hello_world");
        RenameAction action =
                new RenameAction(
                        "Test Rename to PascalCase",
                        StringUtil.RenamingType.PASCAL_CASE,
                        testClass);
        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test"));
        assertEquals("HelloWorld", testClass.getName(), "Class should be renamed to PascalCase");
    }

    @ParameterizedTest
    @EnumSource(StringUtil.RenamingType.class)
    @DisplayName("All renaming types work through action layer")
    void testAllRenamingTypes(StringUtil.RenamingType type) {
        Class testClass = createClass(testPackage, "test_element-name");
        RenameAction action = new RenameAction("Test " + type.name(), type, testClass);
        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test"));

        String newName = testClass.getName();
        assertNotNull(newName, "Name should not be null");

        switch (type) {
            case PASCAL_CASE:
                assertEquals("TestElementName", newName);
                break;
            case CAMEL_CASE:
                assertEquals("testElementName", newName);
                break;
            case SNAKE_CASE:
                assertEquals("test_element_name", newName);
                break;
            case KEBAB_CASE:
                assertEquals("test-element-name", newName);
                break;
            case SCREAMING_SNAKE_CASE:
                assertEquals("TEST_ELEMENT_NAME", newName);
                break;
            case TITLE_CASE:
                assertEquals("Test Element Name", newName);
                break;
            case REVERSE_NAME:
                assertEquals("eman-tnemele_tset", newName);
                break;
        }
    }

    @Test
    @DisplayName("Action executes inside SessionManager session")
    void testSessionManagerIntegration() {
        Class testClass = createClass(testPackage, "session_test");
        RenameAction action =
                new RenameAction("Session Test", StringUtil.RenamingType.CAMEL_CASE, testClass);
        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test"));
        assertEquals("sessionTest", testClass.getName(), "Changes should be committed via session");
    }

    @Test
    @DisplayName("Action wraps service exceptions in RuntimeException")
    void testExceptionHandling() {
        RenameAction action =
                new RenameAction("Error Test", StringUtil.RenamingType.PASCAL_CASE, null);
        assertThrows(
                RuntimeException.class,
                () ->
                        action.actionPerformed(
                                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test")),
                "Action should wrap exceptions in RuntimeException");
    }

    @Test
    @DisplayName("Action supports recursive renaming through service")
    void testRecursiveRenamingThroughAction() {
        Package subPackage = createPackage(testPackage, "child_package");
        Class childClass = createClass(subPackage, "child_class");
        RenameAction action =
                new RenameAction("Recursive Test", StringUtil.RenamingType.KEBAB_CASE, subPackage);
        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test"));
        assertEquals("child-package", subPackage.getName(), "Child package should be renamed");
        assertEquals("child-class", childClass.getName(), "Child class should be renamed");
    }

    /**
     * Helper method to create a Class element within the specified owner package.
     *
     * @param owner The owning package.
     * @param name The name of the class to create.
     * @return The created Class element.
     */
    private Class createClass(Element owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Class " + name);
        Class clazz = project.getElementsFactory().createClassInstance();
        clazz.setName(name);
        clazz.setOwner(owner);
        SessionManager.getInstance().closeSession(project);
        return clazz;
    }

    /**
     * Helper method to create a Package element within the specified owner package.
     *
     * @param owner The owning package.
     * @param name The name of the package to create.
     * @return The created Package element.
     */
    private Package createPackage(Element owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Package " + name);
        Package pkg = project.getElementsFactory().createPackageInstance();
        pkg.setName(name);
        pkg.setOwner(owner);
        SessionManager.getInstance().closeSession(project);
        return pkg;
    }
}
