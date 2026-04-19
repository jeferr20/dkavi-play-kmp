import SwiftUI
import GoogleMaps

@main
struct iOSApp: App {
    init(){
        GMSServices.provideAPIKey("AIzaSyDuMztyZFLE6KDC5U_-mE0BmGMcO8UDln8")
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}