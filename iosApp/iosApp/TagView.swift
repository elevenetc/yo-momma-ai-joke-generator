import Foundation
import Shared
import SwiftUI

struct TagView: View {

    let title: String
    @Binding var selected: Bool

    var body: some View {
        Text(verbatim: title)
            .padding(7)
            .background(
                selected ? RoundedRectangle(cornerRadius: 8).fill(Color.accentColor.opacity(0.2)) :  RoundedRectangle(cornerRadius: 8).fill(Color.gray.opacity(0.2))
            )
            .onTapGesture {
                selected = !selected
                if (selected) {
                    //selectTag(title)
                } else {
                    //deselectTag(title)
                }
            }
    }
}
