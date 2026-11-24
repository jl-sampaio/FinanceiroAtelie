#!/bin/bash
echo "============================================"
echo "  Atelie de Maquiagem - Controle Financeiro"
echo "============================================"
mkdir -p lib out

if [ ! -s "lib/h2.jar" ]; then
    echo "Baixando H2..."
    curl -L "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" -o lib/h2.jar
fi

javac -encoding UTF-8 -cp "lib/h2.jar" -d out src/*.java || { echo "ERRO na compilacao!"; exit 1; }
java -cp "out:lib/h2.jar" Main
