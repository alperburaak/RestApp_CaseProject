package com.alperburaak.restapp.data.remote.ws

import android.util.Log
import com.alperburaak.restapp.data.local.TokenDataStore
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PusherManager(
    private val tokenStore: TokenDataStore,
) {

    private val _orderCreatedFlow = MutableSharedFlow<Pair<Int, String>>(extraBufferCapacity = 10)
    val orderCreatedFlow: SharedFlow<Pair<Int, String>> = _orderCreatedFlow.asSharedFlow()

    private var pusher: Pusher? = null

    // Tek bir bağlantı üzerinden birden fazla kanalı dinleyeceğiz
    fun initConnection() {
        if (pusher != null) return // Zaten bağlıysa tekrar bağlanma

        val authorizer = PusherAuthorizer(tokenStore)
        val options = PusherOptions()
            .setCluster("mt1")
            .setUseTLS(false)
            .setHost("188.34.155.223")
            .setWsPort(6001)
            .setWssPort(6001)
            .setAuthorizer(authorizer)

        pusher = Pusher(WsConfig.KEY, options)
        pusher?.connect()
        Log.d("PUSHER", "Ana bağlantı kuruldu.")
    }

    // Listeyi alıp hepsine abone oluyoruz
    fun subscribeToAllRestaurants(restaurantIds: List<Int>) {
        // Önce bağlantıyı garantile
        initConnection()

        restaurantIds.forEach { id ->
            subscribeToRestaurant(id)
        }
    }

    private fun subscribeToRestaurant(restaurantId: Int) {
        val channelName = "private-restaurant.$restaurantId"

        // Zaten abone olunduysa tekrar olma
        if (pusher?.getPrivateChannel(channelName) != null) {
            Log.d("PUSHER", "Zaten abone olundu: $channelName")
            return
        }

        Log.d("PUSHER", "Abone olunuyor: $channelName")

        val privateListener = object : PrivateChannelEventListener {
            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("PUSHER", "ABONELİK BAŞARILI: $channelName")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.e("PUSHER", "YETKİ HATASI: $message", e)
            }

            override fun onEvent(event: com.pusher.client.channel.PusherEvent) {
                if (event.eventName == "order.created") {

                    _orderCreatedFlow.tryEmit(Pair(restaurantId, event.data))
                }
                Log.d("PUSHER", "EVENT GELDİ ($channelName): ${event.data}")
            }
        }

        val channel = pusher?.subscribePrivate(channelName, privateListener)
        channel?.bind("order.created", privateListener)
    }

    fun disconnect() {
        pusher?.disconnect()
        pusher = null
    }
}