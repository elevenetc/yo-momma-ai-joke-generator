import Foundation
import OrderedCollections
import Shared
import SwiftUI

struct MainScreen: View {

    @State private var jokeGenerationState: LoadingState = LoadingState.Idle.shared
    @State private var showSheet = false
    @State private var totalSheetHeight: CGFloat = .zero
    @State private var genButtonHeight: CGFloat = .zero
    @State private var verticalOffset: CGFloat = 0

    private let service = OpenAIService()
    private let tags = TagKt.TAGS

    var body: some View {


        GeometryReader { _ in
            VStack {
                JokeView(loadingState: $jokeGenerationState)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .offset(y: verticalOffset)
            }
            .sheet(isPresented: .constant(true), content: {
                SheetContent(
                    tags: tags.allTags.map {
                        $0.title
                    },
                    selectionType: tags.selectionType,
                    generateJoke: {
                        jokeGenerationState = LoadingState.Loading.shared
                        service.ask(tags: tags.selectedTags(), completionHandler: { result, error in
                            jokeGenerationState = result ?? LoadingState.Error(error: "completion handler received null")
                        })
                    },
                    selectTag: { tag in
                        let t = tags.tagIdToTag(id: tag)
                        tags.setSelection(tag: t!, selected: true)
                    },
                    deselectTag: { tag in
                        let t = tags.tagIdToTag(id: tag)
                        tags.setSelection(tag: t!, selected: false)
                    },
                    sheetOpened: { opened, shift in
                        if (opened) {
                            withAnimation(.easeInOut) {
                                verticalOffset = (shift / 2) * -1
                            }
                        } else {
                            withAnimation(.easeInOut) {
                                verticalOffset = 0
                            }
                        }
                    }
                )
            })
        }
    }
}