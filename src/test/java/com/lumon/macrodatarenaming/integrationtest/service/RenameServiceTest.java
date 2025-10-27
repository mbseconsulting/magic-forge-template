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
package com.lumon.macrodatarenaming.integrationtest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import com.lumon.macrodatarenaming.service.RenameService;
import com.lumon.macrodatarenaming.util.StringUtil;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.tests.MagicDrawApplication;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;

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
 * Integration tests for RenameService using JUnit 5 with MagicDraw application. Tests business
 * logic that interacts with the MagicDraw model.
 */
@Tag("IntegrationTest")
@ExtendWith(MagicDrawApplication.class)
@DisplayName("RenameService Integration Tests")
class RenameServiceTest {

    private static Project project;
    private Package model;
    private Package testPackage; // Test-specific package for each test
    private String currentTestName; // Store current test name for tearDown

    @BeforeAll
    static void setUpAll() {
        // MagicDrawApplication extension starts MagicDraw before this runs
        project = Application.getInstance().getProjectsManager().createProject();
        assertNotNull(project, "Project should be created by MagicDrawApplication");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        model = project.getPrimaryModel();
        assertNotNull(model, "Primary model should exist");

        // Get current test name (display name if available, otherwise method name)
        currentTestName = testInfo.getDisplayName().replace(" ", "_");

        // Create a package for this specific test
        testPackage = createPackage(model, currentTestName);
        assertNotNull(testPackage, "Test package should be created");
    }

    @AfterAll
    static void tearDown() {
        try {
            String fileName =
                    String.format(
                            "build/test-results/integrationTest/mdzips/RenameServiceTest.mdzip");
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

    /**
     * Test: Single Element Renaming Creates a UML Class with a mixed-case name and renames it to
     * PascalCase.
     */
    @Test
    @DisplayName("Single element renaming with PASCAL_CASE")
    void testSingleElementRenaming() throws Exception {
        // Arrange: Create a Class with name "hello world"
        Class testClass = createClass(testPackage, "hello world");

        // Act: Execute RenameService with PASCAL_CASE
        RenameService renameService =
                new RenameService(StringUtil.RenamingType.PASCAL_CASE, testClass);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Rename", renameService);

        // Assert: Element renamed to "HelloWorld"
        assertTrue(result, "RenameService should return true");
        assertEquals("HelloWorld", testClass.getName(), "Class should be renamed to PascalCase");
    }

    /**
     * Test: Recursive Renaming Creates a Package with nested Classes and Properties, then renames
     * all recursively.
     */
    @Test
    @DisplayName("Recursive renaming of nested elements")
    void testRecursiveRenaming() throws Exception {
        // Arrange: Create Package → Class → Properties
        Package testRecursiveRenaming = createPackage(testPackage, "test_package");
        Class childClass1 = createClass(testRecursiveRenaming, "child_class_one");
        Class childClass2 = createClass(testRecursiveRenaming, "child_class_two");
        Property property1 = createProperty(childClass1, "property_name");
        Property property2 = createProperty(childClass2, "another_property");

        // Act: Execute RenameService on parent Package with CAMEL_CASE
        RenameService renameService =
                new RenameService(StringUtil.RenamingType.CAMEL_CASE, testRecursiveRenaming);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Recursive Rename", renameService);

        // Assert: All children renamed correctly
        assertTrue(result, "RenameService should return true");
        assertEquals(
                "testPackage",
                testRecursiveRenaming.getName(),
                "Package should be renamed to camelCase");
        assertEquals("childClassOne", childClass1.getName(), "Child class 1 should be renamed");
        assertEquals("childClassTwo", childClass2.getName(), "Child class 2 should be renamed");
        assertEquals("propertyName", property1.getName(), "Property 1 should be renamed");
        assertEquals("anotherProperty", property2.getName(), "Property 2 should be renamed");
    }

    @Test
    @DisplayName("Non-editable elements are skipped during rename")
    void testNonEditableElementHandling() throws Exception {
        Profile magicDrawProfile =
                StereotypesHelper.getProfileByURI(
                        project, "http://www.omg.org/spec/UML/20131001/MagicDrawProfile");
        assertNotNull(magicDrawProfile, "MagicDraw Profile should be loaded");

        RenameService renameService =
                new RenameService(StringUtil.RenamingType.CAMEL_CASE, magicDrawProfile);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Non-Editable Rename", renameService);
        assertTrue(result, "RenameService should complete successfully");
        assertEquals(
                "MagicDraw Profile",
                magicDrawProfile.getName(),
                "Non-editable element name should remain unchanged");
    }

    @Test
    @DisplayName("Empty names handled gracefully")
    void testEmptyNameHandling() throws Exception {
        // Arrange: Create elements with empty names
        Class emptyNameClass = createClass(testPackage, "");

        // Act: Execute RenameService with SNAKE_CASE
        RenameService renameService =
                new RenameService(StringUtil.RenamingType.SNAKE_CASE, emptyNameClass);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Empty Names", renameService);

        // Assert: No exceptions thrown, names remain empty
        assertTrue(result, "RenameService should complete successfully");
        assertEquals("", emptyNameClass.getName(), "Empty name should remain empty");
    }

    @ParameterizedTest
    @EnumSource(StringUtil.RenamingType.class)
    @DisplayName("All renaming types transform correctly")
    void testAllRenamingTypes(StringUtil.RenamingType type) throws Exception {
        // Arrange: Create a Class with mixed naming
        String originalName = "test_element-name";
        Class testClass = createClass(testPackage, originalName);

        // Act: Execute RenameService with each type
        RenameService renameService = new RenameService(type, testClass);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test " + type.name(), renameService);

        // Assert: Renamed according to type
        assertTrue(result, "RenameService should return true");
        String newName = testClass.getName();
        assertNotNull(newName, "Name should not be null");

        // Verify specific transformations
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
    @DisplayName("Rename works across different element types")
    void testMultipleElementTypes() throws Exception {
        // Arrange: Create various element types
        Package pkg = createPackage(testPackage, "my_package");
        Class clazz = createClass(pkg, "my_class");
        Interface iface = createInterface(pkg, "my_interface");
        Property property = createProperty(clazz, "my_property");
        Operation operation = createOperation(clazz, "my_operation");

        // Act: Execute RenameService on package with PASCAL_CASE
        RenameService renameService = new RenameService(StringUtil.RenamingType.PASCAL_CASE, pkg);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Multiple Types", renameService);

        // Assert: All element types renamed
        assertTrue(result, "RenameService should return true");
        assertEquals("MyPackage", pkg.getName());
        assertEquals("MyClass", clazz.getName());
        assertEquals("MyInterface", iface.getName());
        assertEquals("MyProperty", property.getName());
        assertEquals("MyOperation", operation.getName());
    }

    @Test
    @DisplayName("Recursive rename handles deep nesting")
    void testDeepNesting() throws Exception {
        // Arrange: Create deeply nested structure
        Package level1 = createPackage(testPackage, "level_one");
        Package level2 = createPackage(level1, "level_two");
        Package level3 = createPackage(level2, "level_three");
        Class deepClass = createClass(level3, "deep_class");

        // Act: Execute RenameService at root with KEBAB_CASE
        RenameService renameService = new RenameService(StringUtil.RenamingType.KEBAB_CASE, level1);
        Boolean result =
                SessionManager.getInstance()
                        .callInsideSession(project, "Test Deep Nesting", renameService);

        // Assert: All levels renamed
        assertTrue(result, "RenameService should return true");
        assertEquals("level-one", level1.getName());
        assertEquals("level-two", level2.getName());
        assertEquals("level-three", level3.getName());
        assertEquals("deep-class", deepClass.getName());
    }

    /**
     * Creates a UML Class in the specified owner.
     *
     * @param owner The owning Element (Package or Class).
     * @param name The name of the Class to create.
     * @return The created Class.
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
     * Creates a UML Package in the specified owner.
     *
     * @param owner The owning Element (usually a Package).
     * @param name The name of the Package to create.
     * @return The created Package.
     */
    private Package createPackage(Element owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Package " + name);
        Package pkg = project.getElementsFactory().createPackageInstance();
        pkg.setName(name);
        pkg.setOwner(owner);
        SessionManager.getInstance().closeSession(project);
        return pkg;
    }

    /**
     * Creates a UML Interface in the specified owner.
     *
     * @param owner The owning Element (Package or Class).
     * @param name The name of the Interface to create.
     * @return The created Interface.
     */
    private Interface createInterface(Element owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Interface " + name);
        Interface iface = project.getElementsFactory().createInterfaceInstance();
        iface.setName(name);
        iface.setOwner(owner);
        SessionManager.getInstance().closeSession(project);
        return iface;
    }

    /**
     * Creates a UML Property in the specified class.
     *
     * @param owner The owning Class.
     * @param name The name of the Property to create.
     * @return The created Property.
     */
    private Property createProperty(Class owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Property " + name);
        Property property = project.getElementsFactory().createPropertyInstance();
        property.setName(name);
        owner.getOwnedAttribute().add(property);
        SessionManager.getInstance().closeSession(project);
        return property;
    }

    /**
     * Creates a UML Operation in the specified class.
     *
     * @param owner The owning Class.
     * @param name The name of the Operation to create.
     * @return The created Operation.
     */
    private Operation createOperation(Class owner, String name) {
        SessionManager.getInstance().createSession(project, "Create Operation " + name);
        Operation operation = project.getElementsFactory().createOperationInstance();
        operation.setName(name);
        owner.getOwnedOperation().add(operation);
        SessionManager.getInstance().closeSession(project);
        return operation;
    }
}
