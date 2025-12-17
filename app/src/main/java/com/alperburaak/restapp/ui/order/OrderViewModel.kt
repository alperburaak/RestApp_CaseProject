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


    init {
        viewModelScope.launch {
            pusherManager.orderCreatedFlow.collect { json ->
                try {
                    val event = gson.fromJson(json, OrderCreatedEvent::class.java)
                    Log.d("VM", "PARSED OK => $event")

                    if (event != null) {
                        val newOrder = Order(
                            order_id = event.order_id ?: return@collect,
                            unique_code = event.unique_code ?: "",
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
                                latitude = event.delivery_address?.latitude ?: 0.0,
                                longitude = event.delivery_address?.longitude ?:0.0
                            ),
                            order_details = event.order_details ?: return@collect,
                            items = event.items ?: emptyList(),
                            created_at = event.created_at ?: ""
                        )
                        _state.update { st ->
                            st.copy(orders = listOf(newOrder) + st.orders)
                        }
                    }


                } catch (e: Exception) {
                    Log.e("VM", "PARSE FAILED! json=$json", e)
                }




            }
        }
    }

    fun getOrderList() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repo.getOrderList()
                .onSuccess { res ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = res.data
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }
    fun accept(orderUniqueCode: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val result = repo.acceptOrder(orderUniqueCode)

        result.onSuccess { resp ->
            Log.d("ORDER", "ACCEPT OK: ${resp.message}")
        }.onFailure { e ->
            Log.e("ORDER", "ACCEPT FAIL", e)
        }

        _state.update { it.copy(isLoading = false) }
    }

    fun reject(orderUniqueCode: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val result = repo.rejectOrder(orderUniqueCode)

        result.onSuccess { resp ->
            Log.d("ORDER", "REJECT OK: ${resp.message}")
        }.onFailure { e ->
            Log.e("ORDER", "REJECT FAIL", e)
        }

        _state.update { it.copy(isLoading = false) }
    }


}
