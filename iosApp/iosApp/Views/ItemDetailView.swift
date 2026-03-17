import SwiftUI
import Shared

struct ItemDetailView: View {
    private let viewModel: DetailViewModel
    private let itemId: String

    @Environment(\.dismiss) private var dismiss
    @State private var state = DetailState(item: nil, isLoading: false, error: nil)

    init(itemId: String) {
        self.itemId = itemId
        self.viewModel = KoinHelper.shared.getDetailViewModel()
    }

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
                    .accessibilityIdentifier("detail_loading")
            } else if let error = state.error {
                VStack(spacing: 12) {
                    Text(error)
                        .foregroundColor(.red)
                        .accessibilityIdentifier("detail_error_message")
                    Button("Retry") {
                        viewModel.onAction(action: DetailActionLoadItem(id: itemId))
                    }
                    .accessibilityIdentifier("detail_retry_button")
                }
                .accessibilityIdentifier("detail_error")
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
                .accessibilityElement(children: .contain)
                .accessibilityIdentifier("detail_screen")
            }
        }
        .navigationTitle(state.item?.title ?? "")
        .task {
            for await newState in viewModel.uiState {
                self.state = newState
            }
        }
        .task {
            viewModel.onAction(action: DetailActionLoadItem(id: itemId))
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
