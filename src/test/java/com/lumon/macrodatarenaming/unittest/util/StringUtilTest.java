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
package com.lumon.macrodatarenaming.unittest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lumon.macrodatarenaming.util.StringUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
@DisplayName("StringUtil transformations")
class StringUtilTest {

    @Test
    @DisplayName("Basic conversions")
    void basicConversions() {
        assertEquals("HelloWorld", StringUtil.toPascal("hello world"));
        assertEquals("helloWorld", StringUtil.toCamel("hello world"));
        assertEquals("hello-world", StringUtil.toKebab("hello world"));
        assertEquals("hello_world", StringUtil.toSnake("hello world"));
        assertEquals("HELLO_WORLD", StringUtil.toScreamingSnake("hello world"));
        assertEquals("Hello World", StringUtil.toTitleCase("hello world"));
        assertEquals("dlrow olleh", StringUtil.reverse("hello world"));
    }

    @Test
    @DisplayName("Mixed separators, case & digits")
    void mixedSeparators() {
        String s = "  hello__world--ID42Parser  ";
        assertEquals("HelloWorldID42Parser", StringUtil.toPascal(s));
        assertEquals("helloWorldID42Parser", StringUtil.toCamel(s));
        assertEquals("hello-world-id-42-parser", StringUtil.toKebab(s));
        assertEquals("hello_world_id_42_parser", StringUtil.toSnake(s));
        assertEquals("HELLO_WORLD_ID_42_PARSER", StringUtil.toScreamingSnake(s));
        assertEquals("Hello World ID 42 Parser", StringUtil.toTitleCase(s));
    }

    @Test
    @DisplayName("Acronyms & digits handled")
    void acronymsAndDigits() {
        String s = "HTTPServer2FA";
        assertEquals("HTTPServer2FA", StringUtil.toPascal(s));
        assertEquals("httpServer2FA", StringUtil.toCamel(s));
        assertEquals("http-server-2-fa", StringUtil.toKebab(s));
        assertEquals("http_server_2_fa", StringUtil.toSnake(s));
        assertEquals("HTTP Server 2 FA", StringUtil.toTitleCase(s));
    }

    @Test
    @DisplayName("Diacritics normalization")
    void diacritics() {
        String s = "crème brûlée café";
        assertEquals("creme-brulee-cafe", StringUtil.toKebab(s));
        assertEquals("Creme Brulee Cafe", StringUtil.toTitleCase(s));
    }

    @Test
    @DisplayName("Minor words in Title Case")
    void titleCaseMinors() {
        assertEquals("The Lord of the Rings", StringUtil.toTitleCase("the lord of the rings"));
    }

    @Test
    @DisplayName("Grapheme-aware reverse (emoji)")
    void reverseEmoji() {
        assertEquals("ediw 😀 elims", StringUtil.reverse("smile 😀 wide"));
    }

    @Test
    @DisplayName("Null & empty inputs")
    void nullAndEmpty() {
        assertEquals("", StringUtil.toCamel(null));
        assertEquals("", StringUtil.toSnake(""));
        assertEquals("", StringUtil.reverse(null));
        assertEquals("", StringUtil.toTitleCase(null));
    }

    @Test
    @DisplayName("Idempotent-ish on already-cased inputs")
    void idempotency() {
        assertEquals("snake_case", StringUtil.toSnake("snake_case"));
        assertEquals("kebab-case", StringUtil.toKebab("kebab-case"));
        assertEquals("PascalCase", StringUtil.toPascal("PascalCase"));
        assertEquals("camelCase", StringUtil.toCamel("camelCase"));
    }

    @Test
    @DisplayName("CJK + Latin + digits")
    void cjkLatinDigits() {
        assertEquals("中文测试-mixed-123-字符", StringUtil.toKebab("中文测试 Mixed123字符"));
    }
}
