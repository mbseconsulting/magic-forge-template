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

import com.nomagic.magicdraw.core.Application;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite that runs all MagicDraw integration tests and properly shuts down the application to
 * release the license.
 *
 * <p>This suite automatically discovers and runs all tests in the integrationtest package.
 */
@Suite
@SelectPackages("com.lumon.macrodatarenaming.integrationtest")
public class IntegrationTestSuite {

    /**
     * Shuts down MagicDraw application after all tests complete. This ensures the license is
     * properly released.
     */
    @AfterSuite
    static void shutdownMagicDraw() {
        System.out.println("Shutting down MagicDraw application to release license...");
        Application application = Application.getInstance();
        if (application != null) {
            try {
                application.shutdown();
            } catch (com.nomagic.runtime.ApplicationExitedException e) {
                // This exception is expected when MagicDraw shuts down properly
                System.out.println("MagicDraw application shutdown complete.");
            } catch (Exception e) {
                System.err.println("Unexpected error during MagicDraw shutdown: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
