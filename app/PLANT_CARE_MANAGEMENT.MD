# 🌿 PlantCare - Smart Crop Management System

**PlantCare** is a native Android application designed for technical monitoring and maintenance of domestic plants. Built with **Kotlin** and **Jetpack Compose**, the app implements a robust architecture to manage personalized irrigation cycles through persistent notifications.

## 🏗️ Software Architecture (MVVM Pattern)

To ensure scalability and component decoupling, the project follows the official **Model-View-ViewModel** pattern:

* **Model (Data Layer):** Powered by **Room Database**. It manages local persistence for plant entities and irrigation logs.
* **View (UI Layer):** Declarative interfaces built with **Jetpack Compose**. They reactively update based on the state exposed by the ViewModel.
* **ViewModel (Business Logic):** Acts as the "Single Source of Truth." It handles CRUD operations logic and coordinates communication with the repository.

---

## 🛠️ Domain Specifications (`Plant` Entity)

Each plant is defined by the following technical parameters:

| Attribute | Type | Description |
| :--- | :--- | :--- |
| `id` | `Long` | Autoincremental Primary Key. |
| `name` | `String` | Identifier name (e.g., Cesar's Cactus). |
| `soilType` | `String` | Substrate type (Universal, Clay, Cactus mix). |
| `potType` | `String` | Pot material (Terracotta, Ceramic, Plastic). |
| `energySource` | `String` | Light requirements (Direct sun, Partial shade, Full shade). |
| `waterInterval` | `Int` | Watering frequency defined in days. |
| `lastWatered` | `Long` | Epoch timestamp of the last confirmed watering event. |

---

## 🔄 Data Management (CRUD Operations)

The application provides full control over the plant collection:

1.  **Create:** Register new species with technical field validation.
2.  **Read:** Dynamic visualization using `LazyColumn` with real-time hydration status.
3.  **Update:** Edit parameters (re-potting, adjusting frequency due to climate).
4.  **Delete:** Physical removal of records from the local database.
5.  **Reset Timer:** "Watering" action that resets the day counter and updates persistence.

---

## 🔔 Notifications & Persistence Logic

To ensure irrigation compliance even when the **app is closed** or after a **device reboot**:

* **WorkManager:** A background `Worker` calculates the delta between `lastWatered` and `currentTime`.
* **Push Notifications:** Upon reaching the `waterInterval`, the system triggers a high-priority notification channel.
* **Background Sync:** Scheduled tasks run independently of the main Activity lifecycle.

---

## 🚀 Development Roadmap

- [x] Environment setup and physical hardware linking.
- [x] Define Room Entity and DAO (Data Access Object).
- [x] Implement `PlantRepository` and `PlantViewModel`.
- [ ] UI Design: Dashboard, Creation Form, and Detail View.
- [ ] WorkManager integration for watering alarms.
- [ ] Camera Module for attaching real photos to plant profiles.

---

> **Lead Developer:** Cesar Palacios  
> **Stack:** Kotlin | Jetpack Compose | Room | WorkManager | MVVM