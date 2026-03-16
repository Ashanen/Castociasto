# Castociasto

A **Kotlin Multiplatform** (KMP) project implementing **Clean Architecture** with **MVI** pattern, targeting Android and iOS with fully shared business logic and native UI.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Module Structure](#module-structure)
- [Dependency Flow](#dependency-flow)
- [Clean Architecture Layers](#clean-architecture-layers)
- [MVI Pattern](#mvi-pattern)
- [SOLID Principles](#solid-principles)
- [Dependency Inversion](#dependency-inversion)
- [Exception Handling](#exception-handling)
- [Dependency Injection](#dependency-injection)
- [Build Convention Plugins](#build-convention-plugins)
- [Testing Strategy](#testing-strategy)
- [Tech Stack](#tech-stack)

---

## Architecture Overview

```mermaid
graph TB
    subgraph Platform["Platform Layer"]
        Android[Android App - Jetpack Compose]
        iOS[iOS App - SwiftUI]
    end

    subgraph Shared["Shared KMP Modules"]
        App[shared:app - DI aggregation + iOS export]

        subgraph Features["Feature Modules"]
            UI[UI - ViewModels + MVI Contracts]
            Domain[Domain - Use Case Implementations]
            Data[Data - Repository Implementations]
        end

        subgraph Core["Core API Modules"]
            CoreAPI[Interfaces + Models only]
        end

        Infra[Infrastructure - Networking]
        Foundation[Foundation - BaseViewModel, Exceptions, Extensions]
    end

    Android --> App
    iOS --> App
    App --> UI
    App --> Domain
    App --> Data
    App --> Infra
    UI --> CoreAPI
    UI --> Foundation
    Domain --> CoreAPI
    Domain --> Foundation
    Data --> CoreAPI
    Data --> Infra
    CoreAPI --> Foundation
    Infra --> Foundation
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
    │   └── networking/                  # Ktor HTTP client, safeApiCall, JSON config
    ├── core/                            # API contracts (interfaces only, no implementations)
    │   ├── items/                       # ItemRepository, GetItemsUseCase, Item model
    │   ├── categories/                  # CategoryRepository, GetCategoriesUseCase, Category model
    │   └── favorites/                   # FavoriteRepository, ToggleFavoriteUseCase
    └── feature/                         # Feature implementations (domain/data/ui per feature)
        ├── items/
        │   ├── domain/                  # GetItemsUseCaseImpl, GetItemUseCaseImpl
        │   ├── data/                    # ApiItemRepository (Ktor)
        │   └── ui/                      # ListViewModel, DetailViewModel, MVI contracts
        ├── categories/
        │   ├── domain/                  # GetCategoriesUseCaseImpl
        │   ├── data/                    # FakeCategoryRepository
        │   └── ui/                      # CategoriesViewModel
        └── favorites/
            ├── domain/                  # ToggleFavoriteUseCaseImpl
            ├── data/                    # FakeFavoriteRepository
            └── ui/                      # FavoritesViewModel
```

---

## Dependency Flow

The dependency graph strictly enforces that **inner layers never depend on outer layers**.

```mermaid
graph LR
    subgraph "Dependency Direction →"
        UI["feature:ui"]
        Domain["feature:domain"]
        Data["feature:data"]
        Core["core:*"]
        Infra["infrastructure:networking"]
        Found["foundation"]
    end

    UI -->|"depends on"| Core
    UI -->|"depends on"| Found
    Domain -->|"depends on"| Core
    Domain -->|"depends on"| Found
    Data -->|"depends on"| Core
    Data -->|"depends on"| Infra
    Core -->|"depends on"| Found
    Infra -->|"depends on"| Found

    style UI fill:#E8D5F5
    style Domain fill:#D5E8F5
    style Data fill:#D5F5E8
    style Core fill:#F5E8D5
    style Infra fill:#F5F5D5
    style Found fill:#F5D5D5
```

Key constraints:
- **UI** never imports Domain or Data implementations
- **Domain** never imports Data or Infrastructure
- **Data** never imports Domain
- **Core** never imports Koin, Ktor, or any implementation detail
- All cross-layer communication happens through **interfaces defined in Core**

---

## Clean Architecture Layers

```mermaid
graph TB
    subgraph "Clean Architecture Layers (outer → inner)"
        direction TB

        L1[PRESENTATION - UI]
        L2[DATA]
        L3[DOMAIN]
        L4[CORE - API CONTRACTS]
        L5[FOUNDATION]
    end

    L1 -->|"calls"| L4
    L2 -->|"implements"| L4
    L3 -->|"implements"| L4
    L3 -->|"depends on"| L4
    L4 -->|"uses"| L5

    style L1 fill:#E8D5F5
    style L2 fill:#D5F5E8
    style L3 fill:#D5E8F5
    style L4 fill:#F5E8D5
    style L5 fill:#F5D5D5
```

| Layer | Module | Responsibility | Allowed Dependencies |
|-------|--------|---------------|---------------------|
| **Foundation** | `shared/foundation` | Base types, exceptions, flow extensions | Coroutines, Lifecycle |
| **Infrastructure** | `shared/infrastructure/networking` | HTTP client, error mapping, serialization | Foundation, Ktor |
| **Core** | `shared/core/*` | Interfaces and models only | Foundation, Coroutines |
| **Domain** | `shared/feature/*/domain` | Use case implementations, business rules | Core, Foundation, Koin |
| **Data** | `shared/feature/*/data` | Repository implementations, DTO mapping | Core, Infrastructure, Koin |
| **UI** | `shared/feature/*/ui` | ViewModels, MVI state machines | Core, Foundation, Koin, Lifecycle |

---

## MVI Pattern

Each screen follows the **Model-View-Intent** pattern with an explicit contract.

```mermaid
sequenceDiagram
    participant View as View (Compose/SwiftUI)
    participant VM as ViewModel
    participant UC as UseCase
    participant Repo as Repository

    View->>VM: onAction(ListAction.LoadItems)
    VM->>VM: _uiState.update { copy(isLoading = true) }
    VM->>UC: getItems()
    UC->>Repo: repository.getItems()
    Repo-->>UC: List<Item>
    UC-->>VM: Flow<List<Item>>
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
    data object LoadItems : ListAction
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
| `GetItemsUseCaseImpl` | Fetches and sorts items |
| `GetItemUseCaseImpl` | Combines item data with favorite status |
| `ApiItemRepository` | HTTP communication for items |
| `ListViewModel` | Manages list screen MVI state machine |
| `safeApiCall` | Maps HTTP exceptions to domain exceptions |

### Open/Closed Principle (OCP)

- New features (e.g., a "bookmarks" feature) are added by creating new modules — no existing code is modified
- The `CastociastoException` sealed hierarchy is extensible with new exception categories
- New use cases implement existing `fun interface` contracts without changing consumers

### Liskov Substitution (LSP)

- `FakeCategoryRepository` and `FakeFavoriteRepository` are drop-in substitutes for real implementations
- Test fakes (`FakeItemRepository`) substitute production repositories without behavioral differences
- All repository implementations honor the contracts defined in `core` interfaces

### Interface Segregation (ISP)

- Use cases are defined as **single-method functional interfaces** (`fun interface`):
  ```kotlin
  fun interface GetItemsUseCase {
      operator fun invoke(): Flow<List<Item>>
  }
  ```
- Repositories expose only the methods needed by their consumers
- Core modules contain only interfaces and models — no implementation baggage

### Dependency Inversion (DIP)

- **High-level modules** (Domain, UI) depend on **abstractions** defined in Core
- **Low-level modules** (Data, Infrastructure) implement those abstractions
- The dependency arrow always points **inward** toward Core

See the [Dependency Inversion](#dependency-inversion) section below for details.

---

## Dependency Inversion

Dependency Inversion is the architectural backbone of this project. The `core` modules define contracts; `domain` and `data` modules provide implementations.

```mermaid
graph TB
    subgraph "core:items (Abstractions)"
        IR["interface ItemRepository"]
        GIU["fun interface GetItemsUseCase"]
        IM["data class Item"]
    end

    subgraph "feature:items:domain (High-level)"
        GIUI["GetItemsUseCaseImpl"]
    end

    subgraph "feature:items:data (Low-level)"
        AIR["ApiItemRepository"]
    end

    subgraph "feature:items:ui (Consumer)"
        LVM["ListViewModel"]
    end

    GIUI -.->|"implements"| GIU
    AIR -.->|"implements"| IR
    GIUI -->|"depends on"| IR
    LVM -->|"depends on"| GIU

    style IR fill:#F5E8D5
    style GIU fill:#F5E8D5
    style IM fill:#F5E8D5
    style GIUI fill:#D5E8F5
    style AIR fill:#D5F5E8
    style LVM fill:#E8D5F5
```

### How it works in practice

1. **Core** defines the interface:
   ```kotlin
   // shared/core/items — no implementation, no Koin, no Ktor
   interface ItemRepository {
       suspend fun getItems(): List<Item>
       suspend fun getItem(id: String): Item?
   }
   ```

2. **Data** provides the implementation:
   ```kotlin
   // shared/feature/items/data — implements the core interface
   internal class ApiItemRepository(
       private val httpClient: HttpClient,
   ) : ItemRepository { ... }
   ```

3. **Domain** depends on the abstraction, not the implementation:
   ```kotlin
   // shared/feature/items/domain — depends only on ItemRepository interface
   internal class GetItemsUseCaseImpl(
       private val repository: ItemRepository,  // interface from core
   ) : GetItemsUseCase { ... }
   ```

4. **Koin** wires it together at runtime:
   ```kotlin
   // Data module registers: interface → implementation
   val itemsDataModule = module {
       single<ItemRepository> { ApiItemRepository(get()) }
   }

   // Domain module consumes: interface only
   val itemsDomainModule = module {
       factory<GetItemsUseCase> { GetItemsUseCaseImpl(get()) }
   }
   ```

5. **Gradle plugins** enforce boundaries at compile time — `domain` cannot import `data`, `core` cannot import Koin.

---

## Exception Handling

Errors are modeled as a **sealed exception hierarchy** that flows from the infrastructure layer up through domain to the UI.

```mermaid
graph TB
    subgraph "Exception Hierarchy"
        CE["CastociastoException"]
        NE["NetworkException"]
        DE["DataException"]

        U["Unauthorized"]
        F["Forbidden"]
        NF["NotFound"]
        SE["ServerError(detail)"]
        NC["NoConnection(detail)"]
        UNK["Unknown(detail)"]

        DNF["DataException.NotFound(detail)"]
    end

    CE --> NE
    CE --> DE
    NE --> U
    NE --> F
    NE --> NF
    NE --> SE
    NE --> NC
    NE --> UNK
    DE --> DNF

    style CE fill:#F5D5D5
    style NE fill:#F5E8D5
    style DE fill:#F5E8D5
```

### Error Flow Through Layers

```mermaid
sequenceDiagram
    participant Ktor as Ktor HTTP Client
    participant Safe as safeApiCall()
    participant Repo as Repository (Data)
    participant UC as UseCase (Domain)
    participant VM as ViewModel (UI)
    participant View as View

    Ktor->>Safe: ClientRequestException (404)
    Safe->>Safe: Map to NetworkException.NotFound
    Safe->>Repo: throw NetworkException.NotFound

    alt Repository handles it
        Repo->>Repo: catch NotFound → return null
        Repo->>UC: null
        UC->>UC: throw DataException.NotFound
    else Repository propagates it
        Repo->>UC: throw NetworkException.NotFound
    end

    UC->>VM: Flow emits error
    VM->>VM: .launchWith(scope) { error → }
    VM->>VM: _uiState.update { copy(error = error.message) }
    VM->>View: uiState with error message
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

**2. Data** — Repositories call `safeApiCall()` and may catch specific exceptions:
```kotlin
override suspend fun getItem(id: String): Item? = try {
    safeApiCall { httpClient.get("posts/$id").body<PostDto>().toItem() }
} catch (e: CastociastoException.NetworkException.NotFound) {
    null  // 404 → null (expected case)
}
```

**3. Domain** — Use cases may throw new domain exceptions:
```kotlin
val item = itemRepository.getItem(id)
    ?: throw CastociastoException.DataException.NotFound("Item $id not found")
```

**4. UI** — ViewModels catch errors via `launchWith()` extension and map to UI state:
```kotlin
getItems()
    .onStart { _uiState.update { it.copy(isLoading = true) } }
    .onEach { items -> _uiState.update { it.copy(items = items, isLoading = false) } }
    .launchWith(viewModelScope) { error ->
        _uiState.update { it.copy(isLoading = false, error = error.message) }
    }
```

---

## Dependency Injection

**Koin** is used as the DI framework. Each architectural layer registers its own Koin module:

```mermaid
graph LR
    subgraph "Koin Module Registration Order"
        NET[networkingModule]

        ID[itemsDataModule]
        CD[categoriesDataModule]
        FD[favoritesDataModule]

        IDO[itemsDomainModule]
        CDO[categoriesDomainModule]
        FDO[favoritesDomainModule]

        IU[itemsUiModule]
        CU[categoriesUiModule]
        FU[favoritesUiModule]
    end

    NET --> ID
    NET --> CD
    NET --> FD
    ID --> IDO
    CD --> CDO
    FD --> FDO
    IDO --> IU
    CDO --> CU
    FDO --> FU

    style NET fill:#F5F5D5
    style ID fill:#D5F5E8
    style CD fill:#D5F5E8
    style FD fill:#D5F5E8
    style IDO fill:#D5E8F5
    style CDO fill:#D5E8F5
    style FDO fill:#D5E8F5
    style IU fill:#E8D5F5
    style CU fill:#E8D5F5
    style FU fill:#E8D5F5
```

All modules are aggregated in `shared/app`:
```kotlin
val appModules = listOf(
    networkingModule,
    itemsDataModule, itemsDomainModule, itemsUiModule,
    categoriesDataModule, categoriesDomainModule, categoriesUiModule,
    favoritesDataModule, favoritesDomainModule, favoritesUiModule,
)
```

---

## Build Convention Plugins

Custom Gradle plugins in `build-logic/convention/` enforce architectural boundaries at compile time:

| Plugin | Applied To | Provides | Restricts |
|--------|-----------|----------|-----------|
| `castociasto.kmp.library` | All shared modules | Coroutines | Base plugin only |
| `castociasto.kmp.api` | `core/*` | Coroutines | No Koin, no Ktor, no Lifecycle |
| `castociasto.kmp.domain` | `feature/*/domain` | Coroutines, Koin | No Ktor, no Lifecycle |
| `castociasto.kmp.data` | `feature/*/data` | Coroutines, Koin | No Lifecycle |
| `castociasto.kmp.ui` | `feature/*/ui` | Coroutines, Koin, Lifecycle | — |
| `castociasto.kmp.infra` | `infrastructure/*` | Ktor, Serialization | No Koin, no Lifecycle |

This means a `core` module **physically cannot** import Koin or Ktor — the dependency simply isn't on the classpath.

---

## Testing Strategy

```mermaid
graph TB
    subgraph "Testing Pyramid"
        E2E[E2E Tests - Appium]
        INT[Integration Tests - Mock HTTP Engine]
        UNIT[Unit Tests - Fakes + Turbine]
    end

    E2E ~~~ INT
    INT ~~~ UNIT

    style E2E fill:#F5D5D5
    style INT fill:#F5E8D5
    style UNIT fill:#D5F5E8
```

| Layer | Test Type | Approach |
|-------|-----------|----------|
| **Domain** | Unit tests | Fake repositories, Turbine for Flow assertions |
| **Data** | Integration tests | Ktor `MockEngine` for HTTP response simulation |
| **UI** | ViewModel tests | Fake use cases, MVI state/effect assertions |
| **E2E** | End-to-end | Appium tests on real devices |

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 2.x (Multiplatform) |
| **Android UI** | Jetpack Compose |
| **iOS UI** | SwiftUI |
| **Architecture** | Clean Architecture + MVI |
| **Networking** | Ktor |
| **Serialization** | kotlinx.serialization |
| **DI** | Koin |
| **Async** | Kotlin Coroutines + Flow |
| **ViewModel** | AndroidX Lifecycle (multiplatform) |
| **Testing** | kotlin.test, Turbine, Ktor MockEngine |
| **E2E Testing** | Appium |
| **Build** | Gradle with convention plugins |
