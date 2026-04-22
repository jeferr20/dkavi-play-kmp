@echo off
echo === Limpiando y Generando Base de Datos SQLDelight ===
call ./gradlew clean generateCommonMainAppDatabaseInterface
echo === Proceso terminado! ===
pause