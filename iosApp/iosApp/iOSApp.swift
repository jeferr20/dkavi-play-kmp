import SwiftUI
import FirebaseCore
import GoogleMaps
import ComposeApp

@main
struct iOSApp: App {
    init() {
        
        FirebaseApp.configure()
        GMSServices.provideAPIKey("AIzaSyDuMztyZFLE6KDC5U_-mE0BmGMcO8UDln8")
        //KoinIOSKt.doInitKoinIOS()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
