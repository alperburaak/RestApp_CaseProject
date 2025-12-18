package com.alperburaak.restapp.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperburaak.restapp.data.remote.model.orderModel.Order
import com.alperburaak.restapp.data.remote.model.orderModel.OrderAddress
import com.alperburaak.restapp.data.remote.model.orderModel.OrderCustomer
import com.alperburaak.restapp.data.remote.model.wsModel.OrderCreatedEvent
import com.alperburaak.restapp.data.repository.OrderRepository
import com.alperburaak.restapp.data.remote.ws.PusherManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repo: OrderRepository,
    private val pusherManager: PusherManager
) : ViewModel() {

    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()
    private val gson = Gson()

    // --- AYRI LİSTELER (GLOBAL CACHE) ---
    // API'den gelen veriler (Veritabanı)
    private val apiOrdersCache = mutableMapOf<Int, List<Order>>()

    // WebSocket'ten gelen veriler (Canlı RAM)
    private val liveOrdersCache = mutableMapOf<Int, MutableList<Order>>()

    // Şu an ekranda hangi restoran var?
    private var currentRestaurantId: Int? = null

    init {
        // WebSocket Dinleyicisi (Tüm kanallardan gelenleri ayıklar)
        viewModelScope.launch {
            pusherManager.orderCreatedFlow.collect { (restaurantId, json) ->
                try {
                    val event = gson.fromJson(json, OrderCreatedEvent::class.java)

                    if (event != null && event.order_id != null) {
                        val lat = event.delivery_address?.latitude?.toDouble() ?: 0.0
                        val lng = event.delivery_address?.longitude?.toDouble() ?: 0.0

                        val newOrder = Order(
                            order_id = event.order_id,
                            unique_code = event.unique_code ?: "",
                            restaurant_id = restaurantId,
                            customer = OrderCustomer(
                                name = event.customer_name ?: "",
                                phone = event.customer_phone ?: "",
                                email = event.customer_email ?: ""
                            ),
                            delivery_address = OrderAddress(
                                full_address = event.delivery_address?.full_address ?: "",
                                city = event.delivery_address?.city ?: "",
                                district = event.delivery_address?.district ?: "",
                                neighborhood = event.delivery_address?.neighborhood ?: "",
                                latitude = lat,
                                longitude = lng
                            ),
                            order_details = event.order_details ?: return@collect,
                            items = event.items ?: emptyList(),
                            created_at = event.created_at ?: ""
                        )

                        // 1. CANLI LİSTEYE EKLE
                        addLiveOrder(restaurantId, newOrder)
                    }
                } catch (e: Exception) {
                    Log.e("VM", "WS Parse Hatası", e)
                }
            }
        }
    }

    // WebSocket'ten gelen siparişi "liveOrdersCache"e ekler
    private fun addLiveOrder(restaurantId: Int, order: Order) {
        val currentLive = liveOrdersCache.getOrPut(restaurantId) { mutableListOf() }

        // Eğer bu sipariş zaten canlı listede yoksa ekle
        if (currentLive.none { it.unique_code == order.unique_code }) {
            // En başa ekle (Yeni sipariş)
            currentLive.add(0, order)
            Log.d("VM", "Restoran $restaurantId için CANLI sipariş eklendi. Kod: ${order.unique_code}")

            // Eğer şu an bu restoranı izliyorsak ekranı güncelle
            if (currentRestaurantId == restaurantId) {
                mergeAndEmit(restaurantId)
            }
        }
    }

    // Ekranda gösterilecek restoranı seç ve verileri yükle
    fun loadOrdersForRestaurant(restaurantId: Int) {
        currentRestaurantId = restaurantId

        // Önce elimizdekileri hemen göster (Loading false yapmadan)
        mergeAndEmit(restaurantId)
        _state.update { it.copy(isLoading = true) }

        // API'den güncel veriyi çek
        viewModelScope.launch {
            repo.getOrderList()
                .onSuccess { res ->
                    // API'den gelen verileri "apiOrdersCache"e yaz (Live cache'e dokunma!)
                    // Not: API tüm restoranları dönüyorsa burada filtrelemek gerekebilir.
                    // Şimdilik gelen listeyi direkt bu restoranın API listesi kabul ediyoruz
                    // veya API filter logic'ine güveniyoruz.

                    val incomingApiOrders = res.data // Eğer restaurant_id varsa .filter { it.restaurant_id == restaurantId }

                    apiOrdersCache[restaurantId] = incomingApiOrders
                    Log.d("VM", "API listesi güncellendi. Adet: ${incomingApiOrders.size}")

                    // Tekrar birleştir ve ekrana bas
                    mergeAndEmit(restaurantId)
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure {
                    // Hata olsa bile canlı siparişler ekranda kalır
                    _state.update { it.copy(isLoading = false, error = it.error) }
                }
        }
    }

    //İki listeyi birleştirip UI'a gönderir
    private fun mergeAndEmit(restaurantId: Int) {
        val apiList = apiOrdersCache[restaurantId] ?: emptyList()
        val liveList = liveOrdersCache[restaurantId] ?: emptyList()

        // Canlı liste ve API listesini birleştir
        // liveList en başta olsun (yeni gelenler)
        // distinctBy ile aynı siparişin (veritabanına yazıldıysa) iki kere görünmesini engelle
        val combinedList = (liveList + apiList).distinctBy { it.unique_code }

        _state.update {
            it.copy(orders = combinedList)
        }
    }

    // Uygulama açıldığında tüm restoranları dinle
    fun startGlobalListening(restaurantIds: List<Int>) {
        if (restaurantIds.isEmpty()) return
        pusherManager.subscribeToAllRestaurants(restaurantIds)
    }

    // Accept/Reject işlemleri
    fun accept(orderUniqueCode: String) = updateLocalStatus(orderUniqueCode, "accepted") { repo.acceptOrder(it) }
    fun reject(orderUniqueCode: String) = updateLocalStatus(orderUniqueCode, "cancelled") { repo.rejectOrder(it) }

    private fun updateLocalStatus(
        uniqueCode: String,
        newStatus: String,
        apiCall: suspend (String) -> Result<Any>
    ) = viewModelScope.launch {


        val activeId = currentRestaurantId ?: return@launch

        // 1. Live Cache Güncelle
        liveOrdersCache[activeId]?.replaceAll {
            if (it.unique_code == uniqueCode) it.copy(order_details = it.order_details.copy(status = newStatus)) else it
        }

        // 2. API Cache Güncelle (List immutable olduğu için map yapıyoruz)
        val currentApiList = apiOrdersCache[activeId] ?: emptyList()
        apiOrdersCache[activeId] = currentApiList.map {
            if (it.unique_code == uniqueCode) it.copy(order_details = it.order_details.copy(status = newStatus)) else it
        }

        // 3. UI Güncelle
        mergeAndEmit(activeId)

        // 4. API İsteği
        apiCall(uniqueCode)
    }
}