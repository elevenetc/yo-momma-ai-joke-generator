import Foundation
import Shared
import SwiftUI
import SwiftUICore

struct SheetContent: View {
    @State private var heights = HeightRecord()
    @State private var showTags = false

    let tags: [String]
    let selectionType: TagSelectionType //TODO: implement multi tag selection
    let generateJoke: () -> Void
    let selectTag: (String) -> Void
    let deselectTag: (String) -> Void
    let sheetOpened: (Bool, CGFloat) -> Void

    private var headerDetent: PresentationDetent {
        PresentationDetent.height((heights.header ?? 10) + 30)
    }

    private var allDetent: PresentationDetent {
        PresentationDetent.height((heights.all ?? 10) + 30)
    }

    @State private var selectedDetent: PresentationDetent = .height(0)

    @State private var toggleStates: [String: Bool]

    init(
        tags: [String],
        selectionType: TagSelectionType,
        generateJoke: @escaping () -> Void,
        selectTag: @escaping (String) -> Void,
        deselectTag: @escaping (String) -> Void,
        sheetOpened: @escaping (Bool, CGFloat) -> Void
    ) {
        self.tags = tags
        self.toggleStates = Dictionary(uniqueKeysWithValues: tags.map {
            ($0, false)
        })
        self.selectionType = selectionType
        self.generateJoke = generateJoke
        self.selectTag = selectTag
        self.deselectTag = deselectTag
        self.sheetOpened = sheetOpened
    }

    var body: some View {

        GeometryReader { _ in
            VStack(alignment: .leading) {

                Button("Generate 'Yo momma' joke") {
                    generateJoke()
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .frame(maxWidth: .infinity)
                .tint(.blue)
                .recordHeight(of: \.header)

                FlexibleView(
                    data: toggleStates.keys.sorted(),
                    spacing: 10,
                    alignment: .center
                ) { item in
                    TagView(
                        title: item,
                        selected: Binding(
                            get: {
                                self.toggleStates[item, default: false]
                            },
                            set: { selected in
                                self.toggleStates.keys.forEach { key in
                                    self.toggleStates[key] = false
                                }
                                self.toggleStates[item] = selected
                                if (selected) {
                                    selectTag(item)
                                } else {
                                    deselectTag(item)
                                }
                            }
                        )
                    )
                }
                .padding(.top)
                .opacity(showTags ? 1.0 : 0.0)
            }
            .padding([.leading, .trailing, .bottom, .top])
            .recordHeight(of: \.all)
        }
        .onPreferenceChange(HeightRecord.self) {
            heights = $0
        }
        .presentationDetents(
            [
                headerDetent,
                allDetent,
            ],
            selection: $selectedDetent
        )
        .onAppear {
            selectedDetent = headerDetent
        }
        .onChange(of: selectedDetent) { newDetent in
            if newDetent == headerDetent {
                sheetOpened(false, heights.all ?? 0)
                withAnimation(Animation.linear(duration: 0.2)) {
                    showTags = false
                }
            } else if newDetent == allDetent {
                sheetOpened(true, heights.all ?? 0)
                withAnimation(Animation.linear(duration: 0.2)) {
                    showTags = true
                }
            }
        }
        .interactiveDismissDisabled()
        .presentationBackgroundInteraction(.enabled)
    }
}

struct HeightRecord: Equatable {
    var header: CGFloat? = nil
    var all: CGFloat? = nil
}

extension HeightRecord: PreferenceKey {
    static var defaultValue = Self()

    static func reduce(value: inout Self, nextValue: () -> Self) {
        value.header = nextValue().header ?? value.header
        value.all = nextValue().all ?? value.all
    }
}

extension View {
    func recordHeight(of keyPath: WritableKeyPath<HeightRecord, CGFloat?>) -> some View {
        return self.background {
            GeometryReader { g in
                var record = HeightRecord()
                let _ = record[keyPath: keyPath] = g.size.height
                Color.clear
                    .preference(
                        key: HeightRecord.self,
                        value: record)
            }
        }
    }
}