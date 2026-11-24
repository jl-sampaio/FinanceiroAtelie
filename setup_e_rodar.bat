@echo off
chcp 65001 >nul
echo ============================================
echo   Atelie de Maquiagem - Controle Financeiro
echo ============================================
echo.

java -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo ERRO: Java nao encontrado! Instale o JDK 17 ou superior: https://adoptium.net
    pause & exit /b
)

mkdir lib 2>nul
mkdir out 2>nul

IF NOT EXIST lib\h2.jar (
    echo Baixando banco de dados H2...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar' -OutFile 'lib\h2.jar'"
)

REM Confere se o JAR foi baixado de verdade (deve ter ~2.5 MB)
for %%A in (lib\h2.jar) do if %%~zA LSS 1000000 (
    echo.
    echo ERRO: lib\h2.jar esta vazio ou incompleto.
    echo Apague o arquivo, baixe manualmente em:
    echo https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar
    echo e salve como lib\h2.jar
    pause & exit /b
)

echo Compilando...
javac -encoding UTF-8 -cp "lib\h2.jar" -d out src\*.java
IF ERRORLEVEL 1 (
    echo ERRO na compilacao!
    pause & exit /b
)

echo Iniciando sistema...
java -cp "out;lib\h2.jar" Main
pause
