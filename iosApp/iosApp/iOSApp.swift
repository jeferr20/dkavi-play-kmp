import SwiftUI
import FirebaseCore
import composeApp
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        
        // 2. Pedir permisos para notificaciones Push
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { _, _ in }
        
        // 3. Registrar para notificaciones remotas
        application.registerForRemoteNotifications()
        
        DispatchQueue.main.async {
            HelperKt.doInitKoin()
            NotificationCenter.default.post(name: NSNotification.Name("KoinReady"), object: nil)
        }
        
        return true
    }
    
    // 4. Pasarle el token de Apple (APNs) a Firebase
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
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
