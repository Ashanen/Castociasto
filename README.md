# Castociasto

A **Kotlin Multiplatform** (KMP) project implementing **Clean Architecture** with **MVI** pattern, targeting Android and iOS with fully shared business logic and native UI.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Module Structure](#module-structure)
- [Dependency Flow](#dependency-flow)
- [Feature Isolation](#feature-isolation)
- [Clean Architecture Layers](#clean-architecture-layers)
- [Event Bus](#event-bus)
- [Database + API Sync](#database--api-sync)
- [Observable vs One-Shot Use Cases](#observable-vs-one-shot-use-cases)
- [MVI Pattern](#mvi-pattern)
- [SOLID Principles](#solid-principles)
- [Dependency Inversion](#dependency-inversion)
- [Exception Handling](#exception-handling)
- [Dependency Injection](#dependency-injection)
- [Build Convention Plugins](#build-convention-plugins)
- [Testing Strategy](#testing-strategy)
- [Getting Started](#getting-started)
- [Tech Stack](#tech-stack)

---

## Architecture Overview

```mermaid
graph TB
    subgraph Platform["Platform - Native UI"]
        Android[Android - Jetpack Compose]
        iOS[iOS - SwiftUI]
    end

    subgraph Shared["Shared KMP"]
        App[shared:app]

        subgraph Feature1["feature:items"]
            IU[items:ui]
            IDom[items:domain]
            IData[items:data]
        end

        subgraph Feature2["feature:categories"]
            CU[categories:ui]
            CDom[categories:domain]
            CData[categories:data]
        end

        subgraph Feature3["feature:favorites"]
            FU[favorites:ui]
            FDom[favorites:domain]
            FData[favorites:data]
        end

        subgraph Core["Core - Domain Contracts"]
            CoreItems[core:items]
            CoreCat[core:categories]
            CoreFav[core:favorites]
            CoreEvents[core:events]
        end

        subgraph Infrastructure
            Infra[infrastructure:networking]
            DB[infrastructure:database]
        end

        Found[foundation]
    end

    Android --> App
    iOS --> App

    IU --> IDom
    IU --> CoreEvents
    IData --> IDom
    IData --> DB
    IData --> CoreEvents
    IDom --> CoreItems
    IDom --> CoreFav
    IData --> Infra

    CU --> CDom
    CData --> CDom
    CDom --> CoreCat

    FU --> FDom
    FData --> FDom
    FData --> CoreEvents
    FDom --> CoreItems
    FDom --> CoreFav

    CoreFav --> CoreItems
    CoreItems --> Found
    CoreCat --> Found
    CoreFav --> Found
    Infra --> Found
```

---

## Module Structure

```
Castociasto/
├── androidApp/                          # Android app (Jetpack Compose)
├── iosApp/                              # iOS app (SwiftUI)
├── build-logic/convention/              # Custom Gradle plugins for layer enforcement
├── e2e/                                 # Appium E2E tests
└── shared/
    ├── app/                             # DI aggregation + iOS framework export
    ├── foundation/                      # Base types: BaseViewModel, exceptions, extensions
    ├── infrastructure/
    │   ├── networking/                  # Ktor HTTP client, safeApiCall, JSON config
    │   └── database/                   # Room database, DAOs, entities
    ├── core/                            # Shared models + cross-feature contracts
    │   ├── items/                       # Item model, ItemRepository interface
    │   ├── categories/                  # Category model, CategoryRepository interface
    │   ├── favorites/                   # FavoriteRepository interface (depends on core:items)
    │   └── events/                     # AppEvent, AppEventBus (cross-feature communication)
    └── feature/                         # Features (domain/data/ui per feature, fully isolated)
        ├── items/
        │   ├── domain/                  # GetItems, ObserveItems, RefreshItems use cases
        │   ├── data/                    # OfflineFirstItemRepository (Room + Ktor)
        │   └── ui/                      # ListViewModel, DetailViewModel, MVI contracts
        ├── categories/
        │   ├── domain/                  # GetCategoriesUseCase + implementation
        │   ├── data/                    # FakeCategoryRepository (in-memory)
        │   └── ui/                      # CategoriesViewModel
        └── favorites/
            ├── domain/                  # GetFavoritesUseCase, ToggleFavoriteUseCase + implementations
            ├── data/                    # FakeFavoriteRepository (in-memory, emits events)
            └── ui/                      # FavoritesViewModel
```

---

## Dependency Flow

Within each feature, **UI and Data both depend on Domain**. Domain is the center — it owns use case interfaces and contracts. Core provides shared models and cross-feature repository interfaces.

```mermaid
graph TB
    subgraph "Within a single feature"
        UI[ui] -->|depends on| DOM[domain]
        DAT[data] -->|depends on| DOM
        DOM -->|depends on| CORE[core]
    end
```

This is the key architectural rule — **UI -> Domain <- Data**:

- `ui` depends on Domain for use case interfaces (and Core for shared models)
- `data` depends on Domain for repository contracts (and Core for shared models)
- `domain` owns use case interfaces and depends on Core for models and cross-feature repository interfaces

**UI never knows about Data. Data never knows about UI.** They are wired together only at runtime through Koin DI.

---

## Feature Isolation

Feature modules **cannot depend on other feature modules**. Each feature is a self-contained vertical slice with its own `ui`, `domain`, and `data` layers.

**Within a single feature** — UI and Data both point to Domain:

```mermaid
graph TB
    subgraph "feature:categories (self-contained)"
        CU[categories:ui] -->|depends on| CD[categories:domain]
        CData[categories:data] -->|depends on| CD
        CD -->|depends on| CoreCat[core:categories]
    end
```

**Cross-feature communication** — features never depend on each other directly. When `favorites` needs the `Item` model or `ItemRepository` from items, it goes through Core. For runtime events, features communicate through the **Event Bus** (`core:events`):

```mermaid
graph TB
    subgraph "feature:favorites"
        FU[favorites:ui] --> FDom[favorites:domain]
        FData[favorites:data] --> FDom
    end

    subgraph "feature:items"
        IU[items:ui] --> IDom[items:domain]
        IData[items:data] --> IDom
    end

    FData -->|emits FavoriteToggled| EB[core:events]
    IData -->|emits ItemsUpdated| EB

    FDom --> CoreFav[core:favorites]
    FDom --> CoreItems[core:items]
    IDom --> CoreItems
    IDom --> CoreFav
    CoreFav --> CoreItems
```

Features share concepts through **Core modules** — never through each other's domain, data, or ui implementations.

---

## Clean Architecture Layers

**Domain** is the center of each feature. It owns use case interfaces and implementations. UI and Data both depend on Domain. Core provides shared models and repository interfaces.

```mermaid
graph TB
    UI[UI - ViewModels, MVI] -->|depends on| Domain
    Data[Data - Repositories] -->|depends on| Domain
    Domain[Domain - Use Case Interfaces + Impls] -->|depends on| Core[Core - Shared Models + Repo Interfaces]
    Core --> Found[Foundation]
    Data -->|uses| Infra[Infrastructure]
    Data -->|uses| DB[Database]
    Infra --> Found
```

| Layer | Module | Role | Depends On |
|-------|--------|------|------------|
| **Domain** | `shared/feature/*/domain` | Use case interfaces, use case implementations, business rules | Core, Foundation |
| **Core** | `shared/core/*` | Shared models, repository interfaces, event bus | Foundation |
| **UI** | `shared/feature/*/ui` | ViewModels, MVI state machines | Domain, Core, Foundation |
| **Data** | `shared/feature/*/data` | Repository implementations, DTO mapping | Domain, Core, Infrastructure, Database |
| **Infrastructure** | `shared/infrastructure/networking` | HTTP client, error mapping, serialization | Foundation |
| **Infrastructure** | `shared/infrastructure/database` | Room database, DAOs, entities | — |
| **Foundation** | `shared/foundation` | BaseViewModel, exception hierarchy, flow extensions | — |

---

## Event Bus

The **Event Bus** (`core:events`) enables cross-module communication without tight coupling. It's a shared contract module — features emit and observe events without knowing about each other.

```mermaid
sequenceDiagram
    participant Fav as FakeFavoriteRepository
    participant EB as AppEventBus
    participant Items as OfflineFirstItemRepository

    Fav->>EB: emit(FavoriteToggled("42"))
    EB-->>Items: FavoriteToggled("42")
    Note over Items: Can react to favorites changing
```

### Events

```kotlin
sealed interface AppEvent {
    data class FavoriteToggled(val itemId: String) : AppEvent
    data object ItemsUpdated : AppEvent
}
```

### AppEventBus

```kotlin
interface AppEventBus {
    val events: SharedFlow<AppEvent>
    suspend fun emit(event: AppEvent)
}
```

The implementation uses a `MutableSharedFlow` with extra buffer capacity for fire-and-forget semantics. It's registered as a singleton in Koin and injected into any module that needs cross-feature communication.

---

## Database + API Sync

The **offline-first** pattern uses Room as a local cache with API as the source of truth. The database module (`infrastructure:database`) provides DAOs and entities, while feature data modules implement the sync strategy.

```mermaid
sequenceDiagram
    participant VM as ListViewModel
    participant UC as ObserveItemsUseCase
    participant Repo as OfflineFirstItemRepository
    participant DAO as ItemDao (Room)
    participant API as Ktor HTTP

    VM->>UC: observeItems()
    UC->>Repo: observeItems()
    Repo->>DAO: observeAll()
    DAO-->>VM: Flow<List<Item>> (continuous)

    VM->>Repo: refresh()
    Repo->>API: GET /posts
    API-->>Repo: List<PostDto>
    Repo->>DAO: upsertAll(entities)
    Note over DAO,VM: Room Flow automatically re-emits updated data
```

### Key patterns

- **`observeItems()`** returns a `Flow` from Room that never completes — UI receives updates automatically when the database changes
- **`refresh()`** fetches from the API and upserts into Room — the observable Flow handles propagation
- **`getItems()` / `getItem()`** are one-shot suspend functions that read from the DAO directly

---

## Observable vs One-Shot Use Cases

The project uses two patterns for use cases, each as a `fun interface`:

### One-shot use cases (fetch once)

```kotlin
fun interface GetItemsUseCase {
    operator fun invoke(): Flow<List<Item>>  // completes after single emission
}

// Implementation uses flowSingle { } — emits once and completes
internal class GetItemsUseCaseImpl(
    private val repository: ItemRepository,
) : GetItemsUseCase {
    override fun invoke() = flowSingle {
        repository.getItems().sortedBy { it.title }
    }
}
```

### Observable use cases (continuous stream)

```kotlin
fun interface ObserveItemsUseCase {
    operator fun invoke(): Flow<List<Item>>  // never completes — emits on every change
}

// Implementation delegates to repository's Flow (backed by Room)
internal class ObserveItemsUseCaseImpl(
    private val repository: ItemRepository,
) : ObserveItemsUseCase {
    override fun invoke() = repository.observeItems()
}
```

### Refresh use case (trigger sync)

```kotlin
fun interface RefreshItemsUseCase {
    operator fun invoke(): Flow<Unit>  // one-shot: triggers API fetch + DB upsert
}
```

### ViewModel usage

```kotlin
// Observe continuously — never completes, UI always up to date
observeItems()
    .onEach { items -> _uiState.update { it.copy(items = items) } }
    .launchWith(viewModelScope) { ... }

// Trigger refresh — one-shot, manages loading state
refreshItems()
    .onStart { _uiState.update { it.copy(isLoading = true) } }
    .onEach { _uiState.update { it.copy(isLoading = false) } }
    .launchWith(viewModelScope) { ... }
```

---

## MVI Pattern

Each screen follows the **Model-View-Intent** pattern with an explicit contract.

```mermaid
sequenceDiagram
    participant View as View (Compose/SwiftUI)
    participant VM as ViewModel
    participant UC as UseCase
    participant Repo as Repository

    View->>VM: onAction(ListAction.Refresh)
    VM->>VM: _uiState.update { copy(isLoading = true) }
    VM->>UC: refreshItems()
    UC->>Repo: repository.refresh()
    Repo-->>UC: Unit
    UC-->>VM: Flow completes
    Note over VM: observeItems() Flow re-emits from Room
    VM->>VM: _uiState.update { copy(items, isLoading = false) }
    VM-->>View: uiState (StateFlow)

    View->>VM: onAction(ListAction.ItemClicked(id))
    VM->>VM: sendEffect(NavigateToDetail(id))
    VM-->>View: sideEffects (Channel)
```

### MVI Contract Structure

Every screen defines a **triple** of `State`, `Action`, and `SideEffect`:

```kotlin
// State — the current UI state (immutable data class)
data class ListState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

// Action — user intents (sealed interface)
sealed interface ListAction {
    data object Refresh : ListAction
    data class ItemClicked(val itemId: String) : ListAction
}

// SideEffect — one-shot events like navigation (sealed interface)
sealed interface ListSideEffect {
    data class NavigateToDetail(val itemId: String) : ListSideEffect
}
```

### BaseViewModel

All ViewModels extend `BaseViewModel<State, Action, SideEffect>`:

```kotlin
abstract class BaseViewModel<S : Any, A, E> : ViewModel() {
    protected abstract val _uiState: MutableStateFlow<S>
    val uiState: StateFlow<S>          // Observed by the View
    val sideEffects: Flow<E>           // Collected by the View for navigation/effects
    abstract fun onAction(action: A)   // Single entry point for all user intents
    protected fun sendEffect(effect: E)
}
```

This ensures **unidirectional data flow**: View dispatches Actions, ViewModel produces State and SideEffects.

---

## SOLID Principles

### Single Responsibility (SRP)

Each module and class has exactly one reason to change:

| Class | Single Responsibility |
|-------|----------------------|
| `ObserveItemsUseCaseImpl` | Continuous item observation from database |
| `RefreshItemsUseCaseImpl` | Triggers API fetch and database sync |
| `GetItemUseCaseImpl` | Combines item data with favorite status |
| `OfflineFirstItemRepository` | Coordinates API, database, and events for items |
| `ListViewModel` | Manages list screen MVI state machine |
| `safeApiCall` | Maps HTTP exceptions to domain exceptions |

### Open/Closed Principle (OCP)

- New features are added by creating new modules — no existing code is modified
- The `CastociastoException` sealed hierarchy is extensible with new exception categories
- New use cases implement existing `fun interface` contracts without changing consumers
- New `AppEvent` subtypes can be added to the sealed interface without modifying existing handlers

### Liskov Substitution (LSP)

- `FakeCategoryRepository` and `FakeFavoriteRepository` are drop-in substitutes for real implementations
- Test fakes (`FakeItemRepository`) substitute production repositories without behavioral differences
- All repository implementations honor the contracts defined in `core` interfaces

### Interface Segregation (ISP)

- Use cases are defined as **single-method functional interfaces** (`fun interface`):
  ```kotlin
  fun interface ObserveItemsUseCase {
      operator fun invoke(): Flow<List<Item>>
  }
  ```
- Repositories expose only the methods needed by their consumers
- Core modules contain only interfaces and models — no implementation baggage

### Dependency Inversion (DIP)

UI and Data depend on **abstractions defined in Domain**. Domain depends on Core for shared models and repository interfaces. No layer knows about another's implementations. See the [Dependency Inversion](#dependency-inversion) section for details.

---

## Dependency Inversion

**Domain** owns use case interfaces. **Core** owns shared models and repository interfaces. UI and Data depend on Domain (and Core for models). None of them know about each other's implementations.

```mermaid
graph TB
    subgraph "feature:items:ui"
        LVM[ListViewModel]
    end

    subgraph "feature:items:domain"
        OIU[fun interface ObserveItemsUseCase]
        OIUI[ObserveItemsUseCaseImpl]
        RIU[fun interface RefreshItemsUseCase]
        RIUI[RefreshItemsUseCaseImpl]
    end

    subgraph "feature:items:data"
        OFR[OfflineFirstItemRepository]
    end

    subgraph "core:items"
        IR[interface ItemRepository]
        IM[data class Item]
    end

    LVM -->|depends on| OIU
    LVM -->|depends on| RIU
    OIUI -.->|implements| OIU
    RIUI -.->|implements| RIU
    OIUI -->|depends on| IR
    RIUI -->|depends on| IR
    OFR -.->|implements| IR
```

### How it works in practice

1. **Core** defines shared models and cross-feature repository interfaces:
   ```kotlin
   // shared/core/items — shared models + repository contract
   data class Item(val id: String, val title: String, val subtitle: String, val isFavorite: Boolean = false)

   interface ItemRepository {
       suspend fun getItems(): List<Item>
       suspend fun getItem(id: String): Item?
       fun observeItems(): Flow<List<Item>>
       fun observeItem(id: String): Flow<Item?>
       suspend fun refresh()
   }
   ```

2. **Domain** defines use case interfaces and provides implementations:
   ```kotlin
   // shared/feature/items/domain — use case interfaces (public)
   fun interface ObserveItemsUseCase {
       operator fun invoke(): Flow<List<Item>>
   }
   fun interface RefreshItemsUseCase {
       operator fun invoke(): Flow<Unit>
   }

   // shared/feature/items/domain — implementations (internal)
   internal class ObserveItemsUseCaseImpl(
       private val repository: ItemRepository,
   ) : ObserveItemsUseCase { ... }
   ```

3. **UI** depends on Domain for use case interfaces:
   ```kotlin
   class ListViewModel(
       private val observeItems: ObserveItemsUseCase,
       private val refreshItems: RefreshItemsUseCase,
   ) : BaseViewModel<ListState, ListAction, ListSideEffect>() { ... }
   ```

4. **Data** depends on Domain (and Core) for repository interfaces:
   ```kotlin
   internal class OfflineFirstItemRepository(
       private val httpClient: HttpClient,
       private val itemDao: ItemDao,
       private val eventBus: AppEventBus,
   ) : ItemRepository { ... }
   ```

5. **Koin** wires it all together at runtime:
   ```kotlin
   val itemsDataModule = module {
       single<ItemRepository> { OfflineFirstItemRepository(get(), get(), get()) }
   }
   val itemsDomainModule = module {
       factory<ObserveItemsUseCase> { ObserveItemsUseCaseImpl(get()) }
       factory<RefreshItemsUseCase> { RefreshItemsUseCaseImpl(get()) }
   }
   val itemsUiModule = module {
       viewModelOf(::ListViewModel)
   }
   ```

6. **Gradle plugins** enforce these boundaries at compile time — `ui` cannot import `data`, `data` cannot import `ui`, `core` cannot import Koin or Ktor.

---

## Exception Handling

Errors are modeled as a **sealed exception hierarchy** that flows from the infrastructure layer up through domain to the UI.

```mermaid
graph TB
    CE[CastociastoException] --> NE[NetworkException]
    CE --> DE[DataException]
    NE --> U[Unauthorized]
    NE --> F[Forbidden]
    NE --> NF[NotFound]
    NE --> SE[ServerError]
    NE --> NC[NoConnection]
    NE --> UNK[Unknown]
    DE --> DNF[DataException.NotFound]
```

### Error Flow Through Layers

```mermaid
sequenceDiagram
    participant Ktor as Ktor HTTP Client
    participant Safe as safeApiCall()
    participant Repo as Repository
    participant UC as UseCase
    participant VM as ViewModel
    participant View as View

    Ktor->>Safe: ClientRequestException (404)
    Safe->>Safe: Map to NetworkException.NotFound
    Safe->>Repo: throw NetworkException.NotFound

    alt Repository handles it
        Repo->>Repo: catch NotFound, return null
        Repo->>UC: null
        UC->>UC: throw DataException.NotFound
    else Repository propagates it
        Repo->>UC: throw NetworkException.NotFound
    end

    UC->>VM: Flow emits error
    VM->>VM: launchWith catches error
    VM->>VM: _uiState.update with error message
    VM->>View: uiState with error
```

### Layer-by-layer error handling

**1. Infrastructure** — `safeApiCall()` maps all HTTP/network errors to typed domain exceptions:
```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): T {
    return try { block() }
    catch (e: CancellationException) { throw e }          // Never swallow cancellation
    catch (e: CastociastoException) { throw e }            // Already typed — re-throw
    catch (e: ClientRequestException) {                     // HTTP 4xx
        throw when (e.response.status.value) {
            401 -> NetworkException.Unauthorized
            403 -> NetworkException.Forbidden
            404 -> NetworkException.NotFound
            else -> NetworkException.Unknown(e.message)
        }
    }
    catch (e: ServerResponseException) { throw NetworkException.ServerError(e.message) }
    catch (e: Exception) { throw NetworkException.NoConnection(e.message ?: "Connection failed") }
}
```

**2. Data** — Repositories call `safeApiCall()` and sync to the database:
```kotlin
override suspend fun refresh() {
    val posts = safeApiCall { httpClient.get("posts").body<List<PostDto>>() }
    itemDao.upsertAll(posts.map { it.toEntity() })
    eventBus.emit(AppEvent.ItemsUpdated)
}
```

**3. Domain** — Use cases may throw new domain exceptions:
```kotlin
val item = itemRepository.getItem(id)
    ?: throw CastociastoException.DataException.NotFound("Item $id not found")
```

**4. UI** — ViewModels catch errors via `launchWith()` extension and map to UI state:
```kotlin
refreshItems()
    .onStart { _uiState.update { it.copy(isLoading = true) } }
    .onEach { _uiState.update { it.copy(isLoading = false) } }
    .launchWith(viewModelScope) { error ->
        _uiState.update { it.copy(isLoading = false, error = error.message) }
    }
```

---

## Dependency Injection

**Koin** is used as the DI framework. Each layer registers its own module. Koin is the only place where implementations are linked to interfaces — the rest of the code only knows about abstractions.

```mermaid
graph LR
    subgraph Infra
        NET[networkingModule]
        DBM[databaseModule]
        EVM[eventsModule]
    end

    subgraph Data
        ID[itemsDataModule]
        CD[categoriesDataModule]
        FD[favoritesDataModule]
    end

    subgraph Domain
        IDO[itemsDomainModule]
        CDO[categoriesDomainModule]
        FDO[favoritesDomainModule]
    end

    subgraph UI
        IU[itemsUiModule]
        CU[categoriesUiModule]
        FU[favoritesUiModule]
    end

    NET --> ID
    DBM --> ID
    EVM --> ID
    EVM --> FD
    ID --> IDO
    CD --> CDO
    FD --> FDO
    IDO --> IU
    CDO --> CU
    FDO --> FU
```

All modules are aggregated in `shared/app`:
```kotlin
val appModules = listOf(
    networkingModule, databaseModule, eventsModule,
    itemsDataModule, itemsDomainModule, itemsUiModule,
    categoriesDataModule, categoriesDomainModule, categoriesUiModule,
    favoritesDataModule, favoritesDomainModule, favoritesUiModule,
)
```

---

## Build Convention Plugins

Custom Gradle plugins in `build-logic/convention/` enforce architectural boundaries at compile time. Each layer gets only the dependencies it needs — nothing more.

| Plugin | Applied To | Provides | Restricts |
|--------|-----------|----------|-----------|
| `castociasto.kmp.library` | All shared modules | KMP base setup | No framework dependencies |
| `castociasto.kmp.api` | `core/*` | Coroutines | No Koin, no Ktor, no Lifecycle |
| `castociasto.kmp.domain` | `feature/*/domain` | Coroutines, Koin | No Ktor, no Lifecycle |
| `castociasto.kmp.data` | `feature/*/data` | Coroutines, Koin | No Lifecycle |
| `castociasto.kmp.ui` | `feature/*/ui` | Coroutines, Koin, Lifecycle, ViewModel | — |
| `castociasto.kmp.infra` | `infrastructure/*` | Ktor, Serialization, Koin | No Lifecycle |

This means a `core` module **physically cannot** import Koin or Ktor — the dependency simply isn't on the classpath. A `domain` module cannot import Ktor. These boundaries are enforced by the build system, not by convention.

---

## Testing Strategy

```mermaid
graph TB
    subgraph "Testing Pyramid"
        E2E[E2E Tests - Appium]
        INT[Integration Tests - Mock HTTP + Fake DAOs]
        UNIT[Unit Tests - Fakes + Turbine]
    end

    E2E ~~~ INT
    INT ~~~ UNIT
```

| Layer | Test Type | Approach |
|-------|-----------|----------|
| **Core** | Unit tests | Event bus emission/subscription with Turbine |
| **Domain** | Unit tests | Fake repositories, Turbine for Flow assertions |
| **Data** | Integration tests | Ktor `MockEngine` + Fake DAOs for offline-first testing |
| **UI** | ViewModel tests | Fake use cases, MVI state/effect assertions |
| **E2E** | End-to-end | Appium + UiAutomator2 on emulators/devices |

### E2E Testing with Appium

End-to-end tests live in the `e2e/` module and use **Appium** with the **UiAutomator2** driver to automate the Android app on real emulators or devices.

#### Compose + Appium: `testTag` as `resource-id`

Jetpack Compose elements are invisible to UiAutomator by default. To make them discoverable:

1. **Enable `testTagsAsResourceId`** once at the root composable (in `MainActivity`):
   ```kotlin
   Box(modifier = Modifier.semantics { testTagsAsResourceId = true }) {
       CastociastoNavHost()
   }
   ```

2. **Add `testTag` to composables** you want to locate in tests:
   ```kotlin
   Scaffold(modifier = Modifier.testTag("list_screen")) { ... }
   LazyColumn(modifier = Modifier.testTag("items_list")) { ... }
   Text("Castociasto", modifier = Modifier.semantics { testTag = "list_title" })
   ```

3. **Find elements in Appium** via `By.id("testTag")`:
   ```kotlin
   driver.findElement(By.id("list_screen"))     // resource-id = "list_screen"
   driver.findElement(By.id("items_list"))       // resource-id = "items_list"
   ```

| Compose API | UiAutomator attribute | Appium locator |
|---|---|---|
| `Modifier.testTag("x")` | `resource-id = "x"` | `By.id("x")` |
| `Icon(contentDescription = "Back")` | `content-desc = "Back"` | `AppiumBy.accessibilityId("Back")` |
| `Text("visible text")` | `text = "visible text"` | `By.xpath("//..[@text='visible text']")` |

#### Appium capabilities

Two capabilities are critical for Compose:

- **`disableIdLocatorAutocompletion = true`** — prevents Appium from prefixing `resource-id` with the package name (Compose `testTag` sets raw strings, not `package:id/tag`)
- **`forceAppLaunch = true`** — restarts the app for each test so it always starts on the list screen

#### Page Object pattern

Tests use the **Page Object Model** — each screen has a page class that encapsulates locators and actions:

```
e2e/src/test/kotlin/.../e2e/
├── base/BaseE2ETest.kt        # Driver setup, capabilities, teardown
├── config/AppiumConfig.kt     # Server URL, timeouts, APK path
├── page/
│   ├── ListPage.kt            # waitForItemsToLoad(), tapFirstItem(), isDisplayed()
│   └── DetailPage.kt          # waitForContent(), tapBack(), isDisplayed()
└── test/ItemsFlowE2ETest.kt   # 5 user journey tests
```

Page methods return page objects for fluent chaining:
```kotlin
listPage.waitForItemsToLoad()
    .tapFirstItem()           // returns DetailPage
    .waitForContent()
    .tapBack()                // returns ListPage
    .waitForItemsToLoad()
```

#### Running E2E tests

See [Getting Started](#getting-started) for full environment setup. Quick run:

```bash
# Terminal 1: start Appium server
appium

# Terminal 2: run tests (emulator must be connected)
./gradlew :e2e:test
```

If Appium server is not running, E2E tests are skipped automatically (not failed).

---

## Getting Started

### 1. Install JDK 17+

```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

### 2. Environment variables

Add **all** of the following to `~/.zshrc` (copy-paste the entire block):

```bash
cat >> ~/.zshrc << 'EOF'

# Java
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# Android SDK (required by Appium and adb)
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH="$PATH:$ANDROID_HOME/platform-tools"
export PATH="$PATH:$ANDROID_HOME/emulator"
EOF
```

Then apply and verify:
```bash
source ~/.zshrc
java -version    # should show 17+
echo $ANDROID_HOME  # should show /Users/<you>/Library/Android/sdk
adb devices      # should list connected devices/emulators
```

> **Important:** After editing `~/.zshrc`, close and reopen **all** terminal tabs — or run `source ~/.zshrc` in each open tab. Appium must be started in a terminal that has `ANDROID_HOME` set.

### 3. Build and run unit tests

```bash
./gradlew testDebugUnitTest
```

### 4. E2E tests (Appium)

#### One-time setup

```bash
npm install -g appium
appium driver install uiautomator2   # Android
appium driver install xcuitest       # iOS
```

#### Run E2E tests (Android)

1. Start an Android emulator (Android Studio -> Device Manager)
2. Build the debug APK:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```
3. Start Appium server in a separate terminal:
   ```bash
   appium
   ```
4. Run the tests:
   ```bash
   ./gradlew :e2e:test
   ```

#### Run E2E tests (iOS)

1. Boot an iOS simulator:
   ```bash
   xcrun simctl boot "iPhone 16"
   open -a Simulator
   ```
2. Build the iOS app:
   ```bash
   xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -configuration Debug build
   ```
3. Start Appium server in a separate terminal:
   ```bash
   appium
   ```
4. Run the tests:
   ```bash
   PLATFORM=ios ./gradlew :e2e:test
   ```
   Optional overrides: `IOS_DEVICE_NAME`, `IOS_PLATFORM_VERSION`, `APP_PATH`.

Always run E2E tests from the **terminal** — Android Studio's test runner may show "Test events were not received" for Appium tests.

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 2.x (Multiplatform) |
| **Android UI** | Jetpack Compose |
| **iOS UI** | SwiftUI |
| **Architecture** | Clean Architecture + MVI |
| **Networking** | Ktor |
| **Database** | Room (KMP) |
| **Serialization** | kotlinx.serialization |
| **DI** | Koin |
| **Async** | Kotlin Coroutines + Flow |
| **ViewModel** | AndroidX Lifecycle (multiplatform) |
| **Testing** | kotlin.test, Turbine, Ktor MockEngine |
| **E2E Testing** | Appium |
| **Build** | Gradle with convention plugins |
