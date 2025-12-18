## 📱 Özellikler
* **Kullanıcı Girişi & Güvenlik:** Token tabanlı kimlik doğrulama (DataStore).
* **Restoran Yönetimi:** Yeni restoran oluşturma ve şubeler arası geçiş.
* **Anlık Sipariş Takibi:** Pusher (WebSocket) ile sayfa yenilemeden sipariş düşmesi.
* **Sipariş Yönetimi:** Gelen siparişleri Onaylama (Accept) veya Reddetme (Cancel).
* **Offline Support:** Siparişlerin önbelleğe (Cache) alınması.
* **Harita Entegrasyonu:** Mapbox SDK ile teslimat adresinin ve kullanıcının konumunun gösterilmesi.

## 🛠 Kullanılan Teknolojiler
* **Dil:** Kotlin
* **UI:** Jetpack Compose (Material3)
* **Mimari:** MVVM (Model-View-ViewModel) & Clean Architecture prensipleri
* **Networking:** Retrofit & OkHttp
* **Realtime:** Pusher (WebSocket)
* **Dependency Injection:** Koin
* **Local Storage:** DataStore (Proto DataStore)
* **Map:** Mapbox Maps SDK
* **Concurrency:** Coroutines & Flow
