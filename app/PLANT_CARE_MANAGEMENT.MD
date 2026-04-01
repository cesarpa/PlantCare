# 🌿 PlantCare - Smart Crop Management System

**PlantCare** is a native Android application designed for technical monitoring and maintenance of domestic plants. Built with **Kotlin** and **Jetpack Compose**, the app implements a robust architecture to manage personalized irrigation cycles through persistent notifications.

## 🏗️ Software Architecture (MVVM Pattern)

To ensure scalability and component decoupling, the project follows the official **Model-View-ViewModel** pattern:

* **Model (Data Layer):** Powered by **Room Database (v2)**. It manages local persistence for plant entities and irrigation logs with automatic destructive migration support.
* **View (UI Layer):** Declarative interfaces built with **Jetpack Compose**. They reactively update based on the state exposed by the ViewModel, including a real-time system clock in the greeting section.
* **ViewModel (Business Logic):** Acts as the "Single Source of Truth." It handles CRUD operations logic and coordinates communication with the repository.

---

## 🛠️ Domain Specifications (`Plant` Entity)

Each plant is defined by the following technical parameters:

| Attribute | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long` | Autoincremental Primary Key. |
| `name` | `String` | Identifier name (e.g., Cesar's Cactus). |
| `soilType` | `SoilType` | Substrate type (Enum: SANDY, LOAMY, CLAYEY, PEATY, SILTY). |
| `potType` | `PotType` | Pot material (Enum: CLAY, PLASTIC, CERAMIC, SELF_WATERING). |
| `energySource` | `EnergySource` | Light requirements (Enum: DIRECT_SUN, PARTIAL_SHADE, FULL_SHADE). |
| `waterInterval` | `Int` | Watering frequency defined in **minutes** (for high granularity). |
| `lastWatered` | `Long` | Epoch timestamp of the last confirmed watering event. |
| `alreadyWatered`| `Boolean` | Computed status flag based on time delta (Default: `true`). |

---

## 🔄 Data Management (CRUD Operations)

The application provides full control over the plant collection:

1.  **Create:** Register new species with technical field validation and automatic initial status calculation.
2.  **Read:** Dynamic visualization using `LazyColumn` with real-time hydration status (✅ "Ya regada" vs 🚿 "Necesita agua").
3.  **Update:** Edit parameters (re-potting, adjusting frequency due to climate).
4.  **Delete:** Physical removal of records from the local database.
5.  **Watering:** Action that updates the `lastWatered` timestamp and refreshes the plant's hydration state.

---

## 🔔 Notifications & Persistence Logic

To ensure irrigation compliance even when the **app is closed** or after a **device reboot**:

* **Real-time Clock:** The Dashboard features a persistent clock that updates every second using a `LaunchedEffect` coroutine.
* **WorkManager (`WateringWorker`):** A background `CoroutineWorker` executes every 15 minutes to calculate the delta: `(currentTime - lastWatered) > waterInterval`.
* **Push Notifications:** When a plant requires attention, the system triggers a high-priority notification channel (`watering_notifications`) with immediate visual feedback.
* **Permissions:** Seamless integration with Android 13+ `POST_NOTIFICATIONS` runtime permissions.

---

## 🚀 Development Roadmap

- [x] Environment setup and physical hardware linking.
- [x] Define Room Entity and DAO (Data Access Object).
- [x] Implement `PlantRepository` and `PlantViewModel`.
- [x] UI Design: Dashboard (with Clock), Creation Form, and Plant List.
- [x] WorkManager integration for watering alarms.
- [x] Image support (Coil) for plant profiles.
- [x] Camera Module for capturing real photos.

---

> **Lead Developer:** Cesar Palacios  
> **Stack:** Kotlin | Jetpack Compose | Room | WorkManager | MVVM
