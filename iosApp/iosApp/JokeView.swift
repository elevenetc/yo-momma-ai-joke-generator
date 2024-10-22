import Foundation
import Shared
import SwiftUI
import SwiftUICore

struct JokeView: View {

    @State private var blurAmount: CGFloat = 0
    @Binding var loadingState: LoadingState

    var body: some View {

        ZStack {
            AngryMommaImage(
                blur: true,
                blurAmount: blurAmount,
                loadingState: $loadingState
            )
            if loadingState == .Idle.shared {
                //nothing, just picture of momma
            } else if loadingState == .Loading.shared {
                ProgressView()
            } else if let error = loadingState as? LoadingState.Error {
                Text("Error: \(error.error)")
                    .multilineTextAlignment(.center)
                    .padding()
            } else if let joke = loadingState as? Joke {
                Text("\(joke.joke)")
                    .multilineTextAlignment(.center)
                    .padding()
            }
        }
    }
}

struct AngryMommaImage: View {

    let blur: Bool
    let blurAmount: CGFloat
    @Binding var loadingState: LoadingState

    var body: some View {
        GeometryReader { geometry in
            Image("angry-momma")
                .resizable()
                .cornerRadius(10)
                .aspectRatio(contentMode: .fit)
                .frame(width: geometry.size.width * 0.5)
                .position(x: geometry.size.width / 2, y: geometry.size.height / 2)
                .blur(radius: loadingState == .Idle.shared ? 0 : 5)
                .opacity(loadingState == .Idle.shared ? 1 : 0.7)
                .animation(.easeInOut, value: loadingState)
        }
    }
}
