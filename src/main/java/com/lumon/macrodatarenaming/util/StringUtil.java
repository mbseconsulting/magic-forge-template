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
package com.lumon.macrodatarenaming.util;

import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * String transformations: - toPascal, toCamel, toKebab, toSnake, toScreamingSnake, toTitleCase,
 * reverse
 *
 * <p>Notes: • Null inputs are treated as empty strings (""). • Tokenization is Unicode-aware: -
 * Splits on non-alphanumerics (anything not \p{L} or \p{Nd}) - Splits on case transitions (fooXML
 * -> foo XML; HTTPServer -> HTTP Server) - Splits between letters and digits (ID42Parser -> ID 42
 * Parser) • Diacritics are stripped for tokenization (NFKD + remove \p{M} marks), which helps
 * produce stable kebab/snake identifiers (e.g., "crème" -> "creme"). • reverse() is grapheme-aware
 * via BreakIterator to avoid breaking emojis/combining marks.
 */
public final class StringUtil {

    private StringUtil() {}

    /**
     * Convert input string to PascalCase.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "HelloWorld"
     *   <li>"hello_world" -> "HelloWorld"
     *   <li>"hello-world" -> "HelloWorld"
     *   <li>"helloWorld" -> "HelloWorld"
     *   <li>"HELLO_WORLD" -> "HelloWorld"
     *   <li>"hello@world" -> "HelloWorld"
     *   <li>"hello!!world" -> "HelloWorld"
     *   <li>"hello world 123" -> "HelloWorld123"
     * </ul>
     *
     * @param input the input string
     * @return the PascalCase version of the input string
     */
    public static String toPascal(String input) {
        List<String> ws = words(input);
        StringBuilder sb = new StringBuilder();
        for (String w : ws) sb.append(cap(w));
        return sb.toString();
    }

    /**
     * Convert input string to camelCase.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "helloWorld"
     *   <li>"hello_world" -> "helloWorld"
     *   <li>"hello-world" -> "helloWorld"
     *   <li>"HelloWorld" -> "helloWorld"
     *   <li>"HELLO_WORLD" -> "helloWorld"
     *   <li>"hello@world" -> "helloWorld"
     *   <li>"hello!!world" -> "helloWorld"
     *   <li>"hello world 123" -> "helloWorld123"
     * </ul>
     *
     * @param input the input string
     * @return the camelCase version of the input string
     */
    public static String toCamel(String input) {
        List<String> ws = words(input);
        if (ws.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(ws.get(0).toLowerCase(Locale.ROOT));
        for (int i = 1; i < ws.size(); i++) sb.append(cap(ws.get(i)));
        return sb.toString();
    }

    /**
     * Convert input string to kebab-case.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "hello-world"
     *   <li>"hello_world" -> "hello-world"
     *   <li>"hello-world" -> "hello-world"
     *   <li>"helloWorld" -> "hello-world"
     *   <li>"HELLO_WORLD" -> "hello-world"
     *   <li>"hello@world" -> "hello-world"
     *   <li>"hello!!world" -> "hello-world"
     *   <li>"hello world 123" -> "hello-world-123"
     * </ul>
     *
     * @param input the input string
     * @return the kebab-case version of the input string
     */
    public static String toKebab(String input) {
        return joinLower(words(input), "-");
    }

    /**
     * Convert input string to snake_case.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "hello_world"
     *   <li>"hello_world" -> "hello_world"
     *   <li>"hello-world" -> "hello_world"
     *   <li>"helloWorld" -> "hello_world"
     *   <li>"HELLO_WORLD" -> "hello_world"
     *   <li>"hello@world" -> "hello_world"
     *   <li>"hello!!world" -> "hello_world"
     *   <li>"hello world 123" -> "hello_world_123"
     * </ul>
     *
     * @param input the input string
     * @return the snake_case version of the input string
     */
    public static String toSnake(String input) {
        return joinLower(words(input), "_");
    }

    /**
     * Convert input string to SCREAMING_SNAKE_CASE.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "HELLO_WORLD"
     *   <li>"hello_world" -> "HELLO_WORLD"
     *   <li>"hello-world" -> "HELLO_WORLD"
     *   <li>"helloWorld" -> "HELLO_WORLD"
     *   <li>"HELLO_WORLD" -> "HELLO_WORLD"
     *   <li>"hello@world" -> "HELLO_WORLD"
     *   <li>"hello!!world" -> "HELLO_WORLD"
     *   <li>"hello world 123" -> "HELLO_WORLD_123"
     * </ul>
     *
     * @param input the input string
     * @return the SCREAMING_SNAKE_CASE version of the input string
     */
    public static String toScreamingSnake(String input) {
        List<String> ws = words(input);
        if (ws.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ws.size(); i++) {
            if (i > 0) sb.append('_');
            sb.append(ws.get(i).toUpperCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /**
     * Convert input string to Title Case.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello world" -> "Hello World"
     *   <li>"hello_world" -> "Hello World"
     *   <li>"hello-world" -> "Hello World"
     *   <li>"helloWorld" -> "Hello World"
     *   <li>"HELLO_WORLD" -> "Hello World"
     *   <li>"hello@world" -> "Hello World"
     *   <li>"hello!!world" -> "Hello World"
     *   <li>"the quick brown fox" -> "The Quick Brown Fox"
     *   <li>"a tale of two cities" -> "A Tale of Two Cities"
     * </ul>
     *
     * @param input the input string
     * @return the Title Case version of the input string
     */
    public static String toTitleCase(String input) {
        List<String> ws = words(input);
        if (ws.isEmpty()) return "";
        Set<String> minors = MINOR_WORDS;
        StringBuilder sb = new StringBuilder();
        int last = ws.size() - 1;
        for (int i = 0; i < ws.size(); i++) {
            String w = ws.get(i);
            if (i > 0) sb.append(' ');
            if (isAcronym(w)) {
                sb.append(w); // preserve acronym casing (ID, URL, HTML...)
            } else if (i != 0 && i != last && minors.contains(w.toLowerCase(Locale.ROOT))) {
                sb.append(w.toLowerCase(Locale.ROOT));
            } else {
                sb.append(cap(w));
            }
        }
        return sb.toString();
    }

    /**
     * Reverse the input string, preserving grapheme clusters.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>"hello" -> "olleh"
     *   <li>"hello world" -> "dlrow olleh"
     *   <li>"123" -> "321"
     *   <li>"Hello World!" -> "!dlroW olleH"
     *   <li>"hello_world_" -> "_dlrow_olleh"
     * </ul>
     *
     * @param input the input string
     * @return the reversed version of the input string
     */
    public static String reverse(String input) {
        String s = nn(input);
        if (s.isEmpty()) return s;
        List<String> clusters = graphemes(s);
        Collections.reverse(clusters);
        StringBuilder sb = new StringBuilder();
        for (String g : clusters) sb.append(g);
        return sb.toString();
    }

    // --- Internal utility methods and constants ---
    // Patterns for tokenization:
    private static final Pattern NON_ALNUM = Pattern.compile("[^\\p{L}\\p{Nd}]+");
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern SPACE = Pattern.compile("\\s+");

    // Boundaries for case/type transitions:
    private static final Pattern[] BOUNDARY_PATTERNS =
            new Pattern[] {
                // lower-or-digit -> UPPER   e.g., "parseXML" -> "parse XML"
                Pattern.compile("(?<=[\\p{Ll}\\p{Nd}])(?=\\p{Lu})"),
                // ACRONYM -> Word           e.g., "HTTPServer" -> "HTTP Server"
                Pattern.compile("(?<=\\p{Lu})(?=\\p{Lu}\\p{Ll})"),
                // letter <-> digit          e.g., "ID42Parser" -> "ID 42 Parser"
                Pattern.compile("(?<=\\p{L})(?=\\p{Nd})"),
                Pattern.compile("(?<=\\p{Nd})(?=\\p{L})")
            };

    // Minor words to keep lowercase in Title Case:
    private static final Set<String> MINOR_WORDS =
            new HashSet<>(
                    Arrays.asList(
                            "a", "an", "the", "and", "or", "but", "for", "nor", "as", "at", "by",
                            "in", "of", "on", "per", "to", "vs", "via", "from", "over", "into",
                            "onto", "up", "down", "off"));

    /**
     * Join words with separator, converting to lower case.
     *
     * <p>E.g., joinLower(["Hello", "World"], "-") -> "hello-world"
     *
     * @param ws the list of words
     * @param sep the separator
     * @return the joined lower-case string
     */
    private static String joinLower(List<String> ws, String sep) {
        if (ws.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ws.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(ws.get(i).toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /**
     * Normalize input string by trimming and removing diacritics.
     *
     * <p>E.g., " crème brûlée " -> "creme brulee"
     *
     * @param input the input string
     * @return the normalized string
     */
    private static String normalize(String input) {
        String s = nn(input).trim();
        if (s.isEmpty()) return s;
        String nfkd = Normalizer.normalize(s, Normalizer.Form.NFKD);
        return DIACRITICS.matcher(nfkd).replaceAll("");
    }

    /**
     * Tokenize input string into words based on non-alphanumerics and case/digit boundaries.
     *
     * <p>E.g., "HelloWorld123_test-case!" -> ["Hello", "World", "123", "test", "case"]
     *
     * @param input the input string
     * @return the list of words
     */
    private static List<String> words(String input) {
        String s = normalize(input);
        if (s.isEmpty()) return Collections.emptyList();

        // Replace non-alphanumerics with single spaces
        s = NON_ALNUM.matcher(s).replaceAll(" ");

        // Insert spaces at boundary transitions
        for (Pattern p : BOUNDARY_PATTERNS) {
            s = p.matcher(s).replaceAll(" ");
        }

        // Split on whitespace
        String[] parts = SPACE.split(s.trim());
        List<String> out = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (!part.isEmpty()) out.add(part);
        }
        return out;
    }

    /**
     * Capitalize the first letter of the word, lowercasing the rest. Preserves acronyms
     * (all-uppercase words of length <= 4).
     *
     * <p>E.g., "hello" -> "Hello", "WORLD" -> "WORLD", "javaScript" -> "Javascript"
     *
     * @param word the input word
     * @return the capitalized word
     */
    private static String cap(String word) {
        if (word == null || word.isEmpty()) return "";
        if (isAcronym(word) && word.length() <= 4) return word;
        String lower = word.toLowerCase(Locale.ROOT);
        int first = lower.codePointAt(0);
        int title = Character.toTitleCase(first);
        StringBuilder sb = new StringBuilder();
        sb.appendCodePoint(title);
        sb.append(lower.substring(Character.charCount(first)));
        return sb.toString();
    }

    /**
     * Check if a word is an acronym (all uppercase letters, at least 2 letters).
     *
     * <p>E.g., "ID" -> true, "XML" -> true, "Hello" -> false, "A" -> false
     *
     * @param w the input word
     * @return true if the word is an acronym, false otherwise
     */
    private static boolean isAcronym(String w) {
        if (w == null || w.isEmpty()) return false;
        int letters = 0;
        for (int i = 0; i < w.length(); ) {
            int cp = w.codePointAt(i);
            if (Character.isLetter(cp)) {
                letters++;
                if (!Character.isUpperCase(cp)) return false;
            }
            i += Character.charCount(cp);
        }
        return letters >= 2;
    }

    /**
     * Split string into grapheme clusters using BreakIterator.
     *
     * <p>E.g., "á😊" -> ["á", "😊"]
     *
     * @param s the input string
     * @return the list of grapheme clusters
     */
    private static List<String> graphemes(String s) {
        List<String> out = new ArrayList<>();
        BreakIterator it = BreakIterator.getCharacterInstance(Locale.ROOT);
        it.setText(s);
        int start = it.first();
        for (int end = it.next(); end != BreakIterator.DONE; end = it.next()) {
            out.add(s.substring(start, end));
            start = end;
        }
        return out;
    }

    /**
     * Null-to-empty helper.
     *
     * @param s the input string
     * @return the input string or "" if null
     */
    private static String nn(String s) {
        return s == null ? "" : s;
    }

    public enum RenamingType {
        PASCAL_CASE,
        CAMEL_CASE,
        SNAKE_CASE,
        KEBAB_CASE,
        SCREAMING_SNAKE_CASE,
        REVERSE_NAME,
        TITLE_CASE,
    }
}
