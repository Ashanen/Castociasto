import SwiftUI
import Shared

struct ItemListView: View {
    private let viewModel: ListViewModel

    @State private var state = ListState(items: [], isLoading: false)
    @State private var selectedItemId: String?

    init() {
        self.viewModel = KoinHelper.shared.getListViewModel()
    }

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
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
                }
            }
        }
        .navigationTitle("Castociasto")
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
