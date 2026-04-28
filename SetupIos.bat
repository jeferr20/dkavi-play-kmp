#!/bin/bash

# =============================================================================
# SETUP SCRIPT - DkaviPlay KMP Project
# =============================================================================
# Ejecutar desde la RAÍZ del proyecto: bash setup_dkaviplay.sh
# Compatible con macOS (Apple Silicon y Intel)
# =============================================================================

set -e  # Detiene el script si algún comando falla

echo "=================================================="
echo "  DkaviPlay KMP - Setup de entorno"
echo "=================================================="

# ------------------------------------------------------------------------------
# PASO 1: Verificar Homebrew
# Homebrew es el gestor de paquetes de macOS, necesario para instalar las
# herramientas del proyecto.
# ------------------------------------------------------------------------------
echo ""
echo "[1/7] Verificando Homebrew..."
if ! command -v brew &> /dev/null; then
    echo "  → Instalando Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
else
    echo "  ✅ Homebrew ya instalado"
fi

# ------------------------------------------------------------------------------
# PASO 2: Verificar CocoaPods
# CocoaPods maneja las dependencias nativas de iOS (Firebase, GoogleMaps, etc.)
# ------------------------------------------------------------------------------
echo ""
echo "[2/7] Verificando CocoaPods..."
if ! command -v pod &> /dev/null; then
    echo "  → Instalando CocoaPods..."
    brew install cocoapods
else
    echo "  ✅ CocoaPods ya instalado: $(pod --version)"
fi

# ------------------------------------------------------------------------------
# PASO 3: Verificar JDK 17
# Gradle y el compilador de Kotlin requieren Java 17 específicamente.
# Se usa Zulu JDK por compatibilidad con Apple Silicon.
# ------------------------------------------------------------------------------
echo ""
echo "[3/7] Verificando JDK 17..."
JAVA_VERSION=$(java -version 2>&1 | head -1 | grep -o '"[0-9.]*"' | tr -d '"' | cut -d. -f1)
if [ "$JAVA_VERSION" != "17" ]; then
    echo "  → Instalando JDK 17 (Zulu)..."
    brew install --cask zulu@17
    echo "  ⚠️  Reinicia la terminal y vuelve a correr el script después de instalar JDK 17"
    exit 1
else
    echo "  ✅ JDK 17 encontrado"
fi

# ------------------------------------------------------------------------------
# PASO 4: Verificar secrets.properties
# Este archivo contiene la API Key de Google Maps y NO debe subirse al repo.
# Crea el archivo manualmente con tu clave antes de continuar.
# ------------------------------------------------------------------------------
echo ""
echo "[4/7] Verificando secrets.properties..."
if [ ! -f "secrets.properties" ]; then
    echo "  ⚠️  No existe secrets.properties"
    echo "  → Creando archivo de ejemplo..."
    echo "MAPS_API_KEY=REEMPLAZA_CON_TU_API_KEY" > secrets.properties
    echo "  ❌ IMPORTANTE: Edita secrets.properties con tu MAPS_API_KEY real antes de continuar"
    echo "     Luego vuelve a correr este script."
    exit 1
else
    echo "  ✅ secrets.properties existe"
fi

# ------------------------------------------------------------------------------
# PASO 5: Generar el Framework Dummy de Kotlin
# El plugin de CocoaPods necesita que exista un framework vacío antes de
# poder correr 'pod install'. Sin este paso, pod install falla con un error
# de "framework doesn't exist yet".
# ------------------------------------------------------------------------------
echo ""
echo "[5/7] Generando framework dummy de Kotlin..."
./gradlew :composeApp:generateDummyFramework
echo "  ✅ Framework dummy generado en composeApp/build/cocoapods/framework/"

# ------------------------------------------------------------------------------
# PASO 6: Instalar dependencias de CocoaPods
# Descarga e integra todas las dependencias nativas de iOS definidas en el
# Podfile: Firebase, GoogleMaps, y el módulo composeApp local.
# La flag --repo-update actualiza el índice de specs de CocoaPods.
# ------------------------------------------------------------------------------
echo ""
echo "[6/7] Instalando pods de iOS..."
cd iosApp
pod install --repo-update
cd ..
echo "  ✅ Pods instalados correctamente"

# ------------------------------------------------------------------------------
# PASO 7: Fix del script de build en el Pods project
# Este es el fix crítico del proyecto. El plugin de CocoaPods genera un script
# con $KOTLIN_PROJECT_PATH vacío y -p apuntando al módulo en vez de la raíz.
# Esto causa el error: "task 'syncFramework' not found in root project".
#
# El fix corrige dos cosas en el podspec generado:
#   1. Reemplaza "$REPO_ROOT" por "$REPO_ROOT/.." en el flag -p
#      para que Gradle apunte a la raíz del proyecto donde vive settings.gradle
#   2. Reemplaza $KOTLIN_PROJECT_PATH:syncFramework por :composeApp:syncFramework
#      para hardcodear el path del módulo ya que la variable llega vacía
# ------------------------------------------------------------------------------
echo ""
echo "[7/7] Aplicando fix al script de CocoaPods (KOTLIN_PROJECT_PATH)..."

PODSPEC_PATH="composeApp/composeApp.podspec"

# Fix 1: corrige el -p para apuntar a la raíz del proyecto
sed -i '' 's|-p "\$REPO_ROOT" \$KOTLIN_PROJECT_PATH:syncFramework|-p "$REPO_ROOT/.." :composeApp:syncFramework|g' "$PODSPEC_PATH"

# Regenerar pods con el podspec corregido
cd iosApp
pod install
cd ..

echo "  ✅ Fix aplicado al podspec y pods reinstalados"

# ------------------------------------------------------------------------------
# LISTO - Abrir el proyecto
# IMPORTANTE: Siempre abrir el .xcworkspace (NO el .xcodeproj).
# El .xcworkspace incluye tanto el proyecto de la app como el de CocoaPods.
# ------------------------------------------------------------------------------
echo ""
echo "=================================================="
echo "  ✅ Setup completado exitosamente"
echo "=================================================="
echo ""
echo "  Abriendo Xcode..."
echo "  ⚠️  IMPORTANTE: Usa SIEMPRE iosApp.xcworkspace, NUNCA .xcodeproj"
echo ""
open iosApp/iosApp.xcworkspace