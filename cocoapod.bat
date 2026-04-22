# Limpiar Gradle
./gradlew clean

# Eliminar carpetas generadas por el plugin de CocoaPods
rm -rf composeApp/build/cocoapods
rm -rf iosApp/Pods
rm -rf iosApp/Podfile.lock

./gradlew :composeApp:generateDummyFramework

cd iosApp
pod install