import SwiftUI
import Shared

struct ItemDetailView: View {
    private let viewModel: DetailViewModel
    private let itemId: String

    @Environment(\.dismiss) private var dismiss
    @State private var state = DetailState(item: nil, isLoading: false)

    init(itemId: String) {
        self.itemId = itemId
        self.viewModel = KoinHelper.shared.getDetailViewModel()
    }

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
            } else if let item = state.item {
                VStack(alignment: .leading, spacing: 16) {
                    Text(item.title)
                        .font(.title)
                        .accessibilityIdentifier("detail_title")
                    Text(item.subtitle)
                        .font(.body)
                        .accessibilityIdentifier("detail_subtitle")
                    Spacer()
                }
                .padding()
            }
        }
        .accessibilityIdentifier("detail_screen")
        .navigationTitle(state.item?.title ?? "")
        .navigationBarBackButtonDisplayMode(.minimal)
        .task {
            viewModel.onAction(action: DetailActionLoadItem(id: itemId))
            for await newState in viewModel.uiState {
                self.state = newState
            }
        }
        .task {
            for await effect in viewModel.sideEffects {
                switch onEnum(of: effect) {
                case .navigateBack:
                    dismiss()
                default:
                    break
                }
            }
        }
    }
}
