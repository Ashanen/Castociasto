import SwiftUI
import Shared

struct ItemListView: View {
    private let viewModel: ListViewModel

    @State private var state = ListState(items: [], isLoading: false, error: nil)
    @State private var selectedItemId: String?

    init() {
        self.viewModel = KoinHelper.shared.getListViewModel()
    }

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
                    .accessibilityIdentifier("list_loading")
            } else if let error = state.error {
                VStack(spacing: 12) {
                    Text(error)
                        .foregroundColor(.red)
                        .accessibilityIdentifier("list_error_message")
                    Button("Retry") {
                        viewModel.onAction(action: ListActionRefresh())
                    }
                    .accessibilityIdentifier("list_retry_button")
                }
                .accessibilityIdentifier("list_error")
            } else {
                List(state.items, id: \.id) { item in
                    Button {
                        viewModel.onAction(action: ListActionItemClicked(itemId: item.id))
                    } label: {
                        VStack(alignment: .leading) {
                            Text(item.title)
                                .font(.headline)
                            Text(item.subtitle)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                    .accessibilityIdentifier("item_\(item.id)")
                }
                .accessibilityIdentifier("items_list")
            }
        }
        .accessibilityIdentifier("list_screen")
        .toolbar {
            ToolbarItem(placement: .principal) {
                Text("Castociasto")
                    .font(.headline)
                    .accessibilityIdentifier("list_title")
            }
        }
        .navigationDestination(item: $selectedItemId) { itemId in
            ItemDetailView(itemId: itemId)
        }
        .task {
            for await newState in viewModel.uiState {
                self.state = newState
            }
        }
        .task {
            for await effect in viewModel.sideEffects {
                switch onEnum(of: effect) {
                case .navigateToDetail(let detail):
                    selectedItemId = detail.itemId
                default:
                    break
                }
            }
        }
    }
}
