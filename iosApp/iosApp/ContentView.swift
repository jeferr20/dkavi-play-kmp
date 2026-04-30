import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State private var isKoinReady = false
    
    var body: some View {
        Group{
            if isKoinReady{
                ComposeView()
                    .ignoresSafeArea()
                    .transition(.opacity)
            }
        }
        .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("KoinReady"))) { _ in
            self.isKoinReady = true
        }
    }
}
