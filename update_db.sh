#!/bin/bash
echo "Generando base de datos..."
./gradlew clean generateCommonMainAppDatabaseInterface

#En la terminal escribes ./update_db.sh