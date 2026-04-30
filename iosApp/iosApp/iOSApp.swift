import SwiftUI
import GoogleMaps
import FirebaseCore
import ComposeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        GMSServices.provideAPIKey(AppConfigGlobal.shared.MAPS_API_KEY)
        
        DispatchQueue.main.async {
            HelperKt.doInitKoin()
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
