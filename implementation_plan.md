# Denzo — Implementation Plan

Build a campus buy/sell Android app for VIT Vellore students, following the [PRD](file:///e:/D%20A%20N%20G%20E%20R/PROJECTS/vit%20platform/prd%201/denzo-prd.md). Sellers list items with prices; buyers make cash offers; the seller picks one; both connect via WhatsApp and settle the deal face-to-face.

## User Review Required

> [!IMPORTANT]
> **Firebase Project Setup**: You will need to create a Firebase project in the [Firebase Console](https://console.firebase.google.com/), enable **Phone Authentication**, **Cloud Firestore**, and **Firebase Storage**, then download the `google-services.json` file and place it in the `app/` directory. This must be done manually before the app can run.

> [!IMPORTANT]
> **App Identity**: The plan uses the package name `com.vit.denzo`. Please confirm this is acceptable, or provide your preferred package name.

> [!WARNING]
> **Phone Auth Testing**: Firebase Phone Auth on emulators requires adding test phone numbers in the Firebase Console. Real device testing with actual SMS costs apply after the free tier (10 SMS/day).

## Open Questions

> [!IMPORTANT]
> 1. **App Name**: The PRD says "Denzo". Should the display name be exactly this, or something shorter like "DealLocker" or "CampusDeals"?
> 2. **Hostel Block List**: What are the exact hostel/block names to include in the dropdown? (e.g., Men's Hostel A, B, C… / Ladies' Hostel A, B… / etc.)
> 3. **"Near Me" Filter Logic**: Should "near me" match only the exact same block, or also adjacent blocks? If adjacent, what defines adjacency?
> 4. **Color Scheme / Branding**: Any preferences for primary colors / theme? The plan defaults to a vibrant dark-mode-first Material 3 dynamic theme with a teal/cyan accent palette.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1+ |
| UI | Jetpack Compose (Material 3) with Compose BOM |
| Architecture | MVVM + Clean Architecture (UI → Domain → Data) |
| DI | Hilt (Dagger) |
| Navigation | Compose Navigation 2.8+ (type-safe `@Serializable` routes) |
| Backend Auth | Firebase Phone Auth (OTP) |
| Database | Cloud Firestore |
| File Storage | Firebase Storage |
| Image Loading | Coil 3 |
| Async | Kotlin Coroutines + Flow |
| Serialization | kotlinx.serialization |

---

## Project Structure

```
com.vit.denzo/
├── App.kt                          // @HiltAndroidApp Application class
├── MainActivity.kt                 // Single-activity, sets Compose content
│
├── core/
│   ├── designsystem/
│   │   ├── theme/
│   │   │   ├── Color.kt            // Color palette tokens
│   │   │   ├── Type.kt             // Typography scale
│   │   │   ├── Shape.kt            // Shape scheme
│   │   │   └── Theme.kt            // Material 3 theme provider
│   │   └── component/              // Reusable composables
│   │       ├── DealButton.kt
│   │       ├── DotBadge.kt         // Green/Red reputation dots
│   │       ├── ItemCard.kt
│   │       ├── OfferCard.kt
│   │       ├── CategoryChip.kt
│   │       ├── PhotoPicker.kt
│   │       └── LoadingOverlay.kt
│   ├── di/
│   │   ├── AppModule.kt            // Provides Firebase instances
│   │   └── RepositoryModule.kt     // Binds repository interfaces
│   ├── model/
│   │   ├── User.kt
│   │   ├── Item.kt
│   │   ├── Offer.kt
│   │   ├── Deal.kt
│   │   └── Category.kt             // Enum of fixed categories
│   └── util/
│       ├── Resource.kt             // Sealed class: Success/Error/Loading
│       ├── Constants.kt            // Collection names, timeouts, block list
│       ├── WhatsAppHelper.kt       // Deep link builder
│       └── Extensions.kt           // Firestore → model mappers
│
├── data/
│   ├── repository/
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── UserRepositoryImpl.kt
│   │   ├── ItemRepositoryImpl.kt
│   │   ├── OfferRepositoryImpl.kt
│   │   ├── DealRepositoryImpl.kt
│   │   └── StorageRepositoryImpl.kt
│   └── source/
│       ├── FirebaseAuthSource.kt
│       ├── FirestoreSource.kt
│       └── FirebaseStorageSource.kt
│
├── domain/
│   ├── repository/
│   │   ├── AuthRepository.kt       // Interface
│   │   ├── UserRepository.kt
│   │   ├── ItemRepository.kt
│   │   ├── OfferRepository.kt
│   │   ├── DealRepository.kt
│   │   └── StorageRepository.kt
│   └── usecase/
│       ├── auth/
│       │   ├── SendOtpUseCase.kt
│       │   └── VerifyOtpUseCase.kt
│       ├── item/
│       │   ├── PostItemUseCase.kt
│       │   ├── GetItemsByCategoryUseCase.kt
│       │   ├── GetNearbyItemsUseCase.kt
│       │   └── RelistItemUseCase.kt
│       ├── offer/
│       │   ├── SubmitOfferUseCase.kt
│       │   ├── GetOffersForItemUseCase.kt
│       │   └── AcceptOfferUseCase.kt  // Batch write: accept one, reject rest, lock item, create deal
│       └── deal/
│           ├── MarkDealCompleteUseCase.kt
│           └── CheckExpiredDealsUseCase.kt
│
├── ui/
│   ├── navigation/
│   │   ├── Routes.kt               // @Serializable route definitions
│   │   └── AppNavHost.kt           // NavHost with all composable destinations
│   ├── auth/
│   │   ├── AuthScreen.kt           // Phone input → OTP input flow
│   │   ├── AuthViewModel.kt
│   │   └── AuthUiState.kt
│   ├── onboarding/
│   │   ├── OnboardingScreen.kt     // Name + block selection
│   │   ├── OnboardingViewModel.kt
│   │   └── OnboardingUiState.kt
│   ├── home/
│   │   ├── HomeScreen.kt           // Category tabs + item feed
│   │   ├── HomeViewModel.kt
│   │   └── HomeUiState.kt
│   ├── postitem/
│   │   ├── PostItemScreen.kt       // Form: category, title, desc, price, photo
│   │   ├── PostItemViewModel.kt
│   │   └── PostItemUiState.kt
│   ├── itemdetail/
│   │   ├── ItemDetailScreen.kt     // View item + submit offer
│   │   ├── ItemDetailViewModel.kt
│   │   └── ItemDetailUiState.kt
│   ├── mylistings/
│   │   ├── MyListingsScreen.kt     // Seller's items + offer counts
│   │   ├── MyListingsViewModel.kt
│   │   └── MyListingsUiState.kt
│   ├── offers/
│   │   ├── OffersScreen.kt         // Seller's offer review screen
│   │   ├── OffersViewModel.kt
│   │   └── OffersUiState.kt
│   ├── deals/
│   │   ├── DealsScreen.kt          // Active deals, mark complete, re-list
│   │   ├── DealsViewModel.kt
│   │   └── DealsUiState.kt
│   └── profile/
│       ├── ProfileScreen.kt        // Green/red dots, edit block
│       ├── ProfileViewModel.kt
│       └── ProfileUiState.kt
```

---

## Proposed Changes

### 1. Project Scaffolding

#### [NEW] Android project via `android create`

Use the `android create empty-activity` CLI command to scaffold a new Compose project with the name "Denzo" and package `com.vit.denzo`.

---

### 2. Gradle Configuration

#### [MODIFY] `gradle/libs.versions.toml`

Add all required dependency versions using the Version Catalog:

| Dependency | Version |
|---|---|
| Compose BOM | `2026.06.01` |
| Firebase BOM | `34.16.0` |
| Hilt | `2.60.1` |
| Navigation Compose | `2.8.8` |
| Coil 3 | `3.5.0` |
| kotlinx-serialization | `1.7.3` |
| Lifecycle | `2.8.7` |
| Coroutines (play-services) | `1.10.1` |

#### [MODIFY] `build.gradle.kts` (project-level)

Apply plugins: `hilt-android`, `ksp`, `kotlin-serialization`, `google-services`.

#### [MODIFY] `app/build.gradle.kts`

- Set `minSdk = 26`, `targetSdk = 35`, `compileSdk = 35`
- Apply all plugins (hilt, ksp, serialization, google-services)
- Add all library dependencies from the version catalog
- Enable Compose build features

#### [NEW] `app/google-services.json`

User must manually download this from Firebase Console after project setup.

---

### 3. Core Layer

#### [NEW] `core/designsystem/theme/` — Material 3 Theme

- **Color.kt**: Dark-mode-first palette with teal/cyan primary, dark surface colors, semantic green/red for reputation dots
- **Type.kt**: Typography scale using Google Font (Inter or Outfit)
- **Shape.kt**: Rounded corner tokens (8dp, 12dp, 16dp, 24dp)
- **Theme.kt**: `denzoTheme` composable wrapping `MaterialTheme`

#### [NEW] `core/designsystem/component/` — Shared UI Components

| Component | Purpose |
|---|---|
| `DealButton.kt` | Styled primary/secondary buttons with loading state |
| `DotBadge.kt` | Green circle (✓) and red circle (✗) with count |
| `ItemCard.kt` | Card showing item photo, title, price, category chip |
| `OfferCard.kt` | Card showing buyer name, offer amount, dots, note |
| `CategoryChip.kt` | Filterable category pill with selected state |
| `PhotoPicker.kt` | Gallery/camera picker composable with preview |
| `LoadingOverlay.kt` | Full-screen semi-transparent loading indicator |

#### [NEW] `core/model/` — Data Models

```kotlin
// Category.kt
enum class Category(val displayName: String) {
    EATABLES("Eatables"),
    WEARABLES("Wearables"),
    CYCLES("Cycles"),
    CALCULATORS("Calculators"),
    LAB_COATS("Lab Coats"),
    SUBSCRIPTIONS("Subscription Plans"),
    STUDY_NOTES("Study Notes"),
    GAME_ACCOUNTS("Game Accounts")
}

// User.kt — maps to users/{uid}
data class User(
    val uid: String, val phone: String, val name: String,
    val block: String, val greenDots: Int, val redDots: Int,
    val createdAt: Timestamp
)

// Item.kt — maps to items/{id}
data class Item(
    val id: String, val sellerId: String, val category: String,
    val title: String, val description: String, val price: Double,
    val photoUrl: String, val status: String, val createdAt: Timestamp
)

// Offer.kt — maps to items/{id}/offers/{offerId}
data class Offer(
    val id: String, val buyerId: String, val amount: Double,
    val note: String?, val status: String, val createdAt: Timestamp
)

// Deal.kt — maps to deals/{id}
data class Deal(
    val id: String, val itemId: String, val sellerId: String,
    val buyerId: String, val finalPrice: Double, val lockedAt: Timestamp,
    val completionDeadline: Timestamp, val status: String,
    val completedAt: Timestamp?
)
```

#### [NEW] `core/util/Resource.kt`

Sealed class wrapper (`Success`, `Error`, `Loading`) for repository results.

#### [NEW] `core/util/Constants.kt`

Firestore collection names, 3-day completion window, hostel block list.

#### [NEW] `core/util/WhatsAppHelper.kt`

Deep link builder: `https://wa.me/{phone}?text={encoded_message}` with fallback to browser if WhatsApp not installed.

#### [NEW] `core/di/AppModule.kt` & `RepositoryModule.kt`

Hilt `@Module` providing `FirebaseAuth`, `FirebaseFirestore`, `FirebaseStorage` singletons, and binding repository interfaces to implementations.

---

### 4. Data Layer

#### [NEW] `data/repository/AuthRepositoryImpl.kt`

- `requestOtp(phone, activity)` → `callbackFlow` wrapping `PhoneAuthProvider.verifyPhoneNumber()`
- `verifyOtp(verificationId, code)` → `signInWithCredential().await()`
- `getCurrentUser()` → `FirebaseAuth.currentUser`

#### [NEW] `data/repository/UserRepositoryImpl.kt`

- `createUser(user)` → Firestore `set()` on `users/{uid}`
- `getUser(uid)` → Firestore `get()` with model mapping
- `updateDots(uid, greenDelta, redDelta)` → `FieldValue.increment()`

#### [NEW] `data/repository/ItemRepositoryImpl.kt`

- `postItem(item)` → Firestore `add()` to `items/`
- `getItemsByCategory(category)` → Firestore query with `.whereEqualTo("category", …).whereEqualTo("status", "open")`
- `getNearbyItems(category, block)` → additional `.whereIn("sellerBlock", nearbyBlocks)`
- `updateItemStatus(itemId, status)` → Firestore `update()`
- Real-time feed via `snapshotFlow` on Firestore queries

#### [NEW] `data/repository/OfferRepositoryImpl.kt`

- `submitOffer(itemId, offer)` → Firestore `add()` to `items/{id}/offers/`
- `getOffersForItem(itemId)` → query ordered by `amount` descending, real-time via snapshots
- `updateOfferStatus(itemId, offerId, status)` → Firestore `update()`

#### [NEW] `data/repository/DealRepositoryImpl.kt`

- `createDeal(deal)` → Firestore `set()` on `deals/{id}`
- `getDealsForUser(uid)` → query where `sellerId == uid OR buyerId == uid`
- `markComplete(dealId)` → update `status` to "completed", set `completedAt`
- `getExpiredDeals()` → query where `status == "locked"` and `completionDeadline < now`

#### [NEW] `data/repository/StorageRepositoryImpl.kt`

- `uploadImage(uri, path)` → `StorageReference.putFile().await()` → return download URL

---

### 5. Domain Layer (Use Cases)

#### [NEW] Key Use Cases

| Use Case | Logic |
|---|---|
| `SendOtpUseCase` | Calls `authRepository.requestOtp()`, returns Flow of verification state |
| `VerifyOtpUseCase` | Calls `authRepository.verifyOtp()`, returns success/failure |
| `PostItemUseCase` | Validates fields → uploads photo via `StorageRepository` → creates item doc via `ItemRepository` |
| `AcceptOfferUseCase` | **Critical batch write**: set accepted offer → `accepted`, all other offers → `rejected`, item → `locked`, create `Deal` doc with `completionDeadline = now + 3 days` |
| `MarkDealCompleteUseCase` | Updates deal status → if both sides confirmed, +1 green dot to both users |
| `RelistItemUseCase` | Sets item back to `open`, deal to `expired`, +1 red dot to buyer |

---

### 6. UI Layer (Screens)

#### [NEW] `ui/navigation/Routes.kt`

Type-safe routes using `@Serializable`:

```kotlin
@Serializable data object AuthRoute
@Serializable data object OnboardingRoute
@Serializable data object HomeRoute
@Serializable data object PostItemRoute
@Serializable data class ItemDetailRoute(val itemId: String)
@Serializable data object MyListingsRoute
@Serializable data class OffersRoute(val itemId: String)
@Serializable data object DealsRoute
@Serializable data object ProfileRoute
```

#### [NEW] `ui/navigation/AppNavHost.kt`

Single `NavHost` with all destinations, conditional start destination (Auth vs Home based on login state).

#### [NEW] `ui/auth/` — Phone Auth Flow

- **AuthScreen**: Phone number input field → "Send OTP" button → OTP input field → "Verify" button
- **AuthViewModel**: Manages OTP flow state via `StateFlow<AuthUiState>`
- States: `Idle → Loading → CodeSent → Verifying → Success / Error`

#### [NEW] `ui/onboarding/` — Profile Setup

- **OnboardingScreen**: Name text field + hostel/block dropdown (exposed dropdown menu) + "Get Started" button
- Saves user profile to Firestore on completion

#### [NEW] `ui/home/` — Browse Feed

- **HomeScreen**: 
  - Top: Horizontal scrollable category chips (all 8 categories + "All")
  - Toggle: "All Campus" / "Near Me" filter
  - Body: Vertical lazy grid of `ItemCard`s
  - FAB: "+" to navigate to PostItemScreen
  - Bottom nav: Home / My Listings / Deals / Profile

#### [NEW] `ui/postitem/` — List an Item

- **PostItemScreen**: Form with category dropdown, title, description, price input, photo picker (gallery/camera)
- Validation: all fields required, price > 0, photo required
- On submit: upload photo → create item → navigate back

#### [NEW] `ui/itemdetail/` — View Item + Make Offer

- **ItemDetailScreen**: Full photo, title, description, price, seller info (name, block, dots)
- "Make an Offer" section: amount input + optional note + submit button
- Shows "Your offer submitted" state if buyer already offered

#### [NEW] `ui/mylistings/` — Seller's Items

- **MyListingsScreen**: List of seller's items with status badges (open/locked/sold) + offer count per item
- Tap an item → navigate to OffersScreen

#### [NEW] `ui/offers/` — Offer Review (Seller)

- **OffersScreen**: List of offers for a specific item, sorted by amount (highest first)
- Each offer shows: buyer name, amount, optional note, green/red dot counts
- "Accept" button per offer → triggers `AcceptOfferUseCase` batch write

#### [NEW] `ui/deals/` — Active Deals

- **DealsScreen**: Shows locked deals for the current user (as buyer or seller)
- Each deal card shows: item title, counterparty name, final price, time remaining
- Actions: "Mark as Completed" button, "Re-list" button (seller only, after deadline)
- WhatsApp deep link button to contact the counterparty

#### [NEW] `ui/profile/` — User Profile

- **ProfileScreen**: User name, phone, hostel block, green dot count, red dot count
- Edit block option
- Sign out button

---

### 7. Bottom Navigation

4-tab bottom bar integrated into the `HomeScreen` scaffold:

| Tab | Icon | Destination |
|---|---|---|
| Browse | 🏠 Home | `HomeRoute` |
| My Listings | 📦 Inventory | `MyListingsRoute` |
| Deals | 🤝 Handshake | `DealsRoute` |
| Profile | 👤 Person | `ProfileRoute` |

---

### 8. Firestore Security Rules (Basic v1)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == uid;
    }
    match /items/{itemId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && (request.auth.uid == resource.data.sellerId);
    }
    match /items/{itemId}/offers/{offerId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null;
    }
    match /deals/{dealId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && (request.auth.uid == resource.data.sellerId
         || request.auth.uid == resource.data.buyerId);
    }
  }
}
```

---

## Build Phases & Execution Order

### Phase 1 — Scaffolding & Core (Week 1, Part 1)
1. Create project with `android create empty-activity`
2. Configure Gradle (version catalog, dependencies, plugins)
3. Implement design system (theme, colors, typography, shapes)
4. Build shared components (`ItemCard`, `DotBadge`, `CategoryChip`, etc.)
5. Set up Hilt DI modules
6. Define data models & `Resource` wrapper

### Phase 2 — Auth & Profile (Week 1, Part 2)
7. Implement `AuthRepository` (Phone OTP flow)
8. Build Auth screen (phone → OTP → verify)
9. Implement `UserRepository`
10. Build Onboarding screen (name + block)
11. Build Profile screen (dots display, sign out)
12. Set up Navigation with auth-conditional start

### Phase 3 — Listings & Browse (Week 2)
13. Implement `StorageRepository` (photo upload)
14. Implement `ItemRepository` (CRUD + real-time queries)
15. Build Post Item screen (form + photo picker + upload)
16. Build Home screen (category filter + near-me toggle + item grid)
17. Build Item Detail screen (view item info)
18. Build My Listings screen (seller's items)

### Phase 4 — Offers & Deals (Week 3)
19. Implement `OfferRepository`
20. Add offer submission to Item Detail screen
21. Build Offers screen (seller reviews, sorted by price, dots visible)
22. Implement `AcceptOfferUseCase` (batch write)
23. Implement `DealRepository`
24. Build Deals screen (active deals list)
25. Add "Mark as Completed" flow + dot increment logic
26. Add "Re-list" flow + red dot logic
27. Add WhatsApp deep link button

### Phase 5 — Polish (Week 4 / Buffer)
28. Add animations (screen transitions, card interactions, loading states)
29. Add error handling & empty states for all screens
30. Add pull-to-refresh on feed screens
31. Final UI polish & responsive layout checks
32. Testing with real data & edge cases

---

## Verification Plan

### Automated Tests
- **Unit tests** for all use cases (especially `AcceptOfferUseCase` batch logic and `MarkDealCompleteUseCase` dot counting)
- **Repository tests** with mocked Firestore instances
- Run via: `./gradlew test`

### Manual Verification
- Build and run on emulator: `android run` or `./gradlew installDebug`
- Test full flow: Sign up → Post item → Browse → Make offer → Accept offer → WhatsApp link → Mark complete → Verify dots
- Test edge cases: expired deals, re-listing, multiple offers on same item
- Test on real device with actual SMS OTP
