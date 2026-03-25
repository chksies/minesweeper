#!/bin/bash
# build.sh — compile and run Minesweeper
# Requires: JDK 11+

SRC_DIR="src"
OUT_DIR="out"

echo "=== Compiling Minesweeper ==="
mkdir -p "$OUT_DIR"

# Collect all .java files
find "$SRC_DIR" -name "*.java" > sources.txt

# Compile
javac -d "$OUT_DIR" @sources.txt
STATUS=$?
rm sources.txt

if [ $STATUS -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Compilation successful. Starting game..."
echo ""
java -cp "$OUT_DIR" minesweeper.Main
