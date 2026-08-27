import SwiftUI
import FirebaseCore
import composeApp

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        
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
