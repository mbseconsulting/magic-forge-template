# === CODE SHRINKING & OBFUSCATION ===
-overloadaggressively
-dontnote **

# Ignore warnings about Jython classes from CATIA Magic libraries
# These are library dependencies with Python-compiled classes that don't follow Java naming conventions
-ignorewarnings

# Adapt strings and resource file references
-adaptclassstrings
-renamesourcefileattribute SourceFile
