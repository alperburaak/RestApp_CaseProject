package com.alperburaak.restapp.data.remote.ws

import android.util.Log
import com.alperburaak.restapp.data.local.TokenDataStore
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class PusherManager(
    private val tokenStore: TokenDataStore,

) {

    private val _orderCreatedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val orderCreatedFlow: SharedFlow<String> = _orderCreatedFlow.asSharedFlow()
    private var pusher: Pusher? = null

    fun connectAndSubscribe(restaurantId: Int) {
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

        val channelName = "private-restaurant.$restaurantId"
        Log.d("PUSHER", "Subscribing to: $channelName")

        val privateListener = object : PrivateChannelEventListener {
            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("PUSHER", "SUBSCRIBED: $channelName")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.e("PUSHER", "AUTH FAIL: $message", e)
            }

            override fun onEvent(event: com.pusher.client.channel.PusherEvent) {
                if (event.eventName == "order.created") {
                    _orderCreatedFlow.tryEmit(event.data)
                }
                Log.d("PUSHER", "EVENT name=${event.eventName} data=${event.data}")
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
