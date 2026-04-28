import SwiftUI
import GoogleMaps
import FirebaseCore
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        // Inicializa Firebase aquí (es el punto más temprano posible)
        print("🔥 Firebase configurado")
        FirebaseApp.configure()
        
        // Inicializa Google Maps
        GMSServices.provideAPIKey(AppConfigGlobal.shared.MAPS_API_KEY)
        
        // 2. Ejecutar Koin con un pequeño retraso
                // Esto permite que el SDK nativo de Firebase termine de inicializar sus hilos internos
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            HelperKt.doInitKoin()
            print("📦 Koin inicializado después de Firebase")
            NotificationCenter.default.post(name: NSNotification.Name("KoinReady"), object: nil)
        }
        
        return true
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
