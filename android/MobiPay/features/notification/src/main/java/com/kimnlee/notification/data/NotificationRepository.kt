package com.kimnlee.notification.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import com.kimnlee.common.utils.LocalDateTimeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val paymentRequestKey = "payment_requests"
    private val invitationMessagesKey = "invitation_messages"
    private val paymentSuccessKey = "payment_success"

    private val _paymentSuccessMessages = MutableStateFlow<List<Notification>>(emptyList())
    val paymentSuccessMessages: StateFlow<List<Notification>> = _paymentSuccessMessages.asStateFlow()

    private val _paymentRequestMessages = MutableStateFlow<List<Notification>>(emptyList())
    val paymentRequestMessages: StateFlow<List<Notification>> = _paymentRequestMessages.asStateFlow()

    private val _invitationMessages = MutableStateFlow<List<Notification>>(emptyList())
    val invitationMessages: StateFlow<List<Notification>> = _invitationMessages.asStateFlow()

    val allNotifications: StateFlow<List<Notification>> = MutableStateFlow(emptyList())

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val paymentJson = sharedPreferences.getString(paymentRequestKey, null)
        val invitationJson = sharedPreferences.getString(invitationMessagesKey, null)
        val paymentSuccessJson = sharedPreferences.getString(paymentSuccessKey, null)

        _paymentSuccessMessages.value = if (paymentSuccessJson != null) {
            gson.fromJson(paymentSuccessJson, object : TypeToken<List<Notification>>() {}.type)
        } else {
            emptyList()
        }

        _paymentRequestMessages.value = if (paymentJson != null) {
            gson.fromJson(paymentJson, object : TypeToken<List<Notification>>() {}.type)
        } else {
            emptyList()
        }

        _invitationMessages.value = if (invitationJson != null) {
            gson.fromJson(invitationJson, object : TypeToken<List<Notification>>() {}.type)
        } else {
            emptyList()
        }

        updateAllNotifications()
    }

    private fun updateAllNotifications() {
        (allNotifications as MutableStateFlow).value = (_paymentSuccessMessages.value + _invitationMessages.value)
            .sortedByDescending { it.timestamp }
    }


//    var paymentRequestMessages: MutableList<Notification>
//        get() {
//            val json = sharedPreferences.getString(paymentRequestKey, null)
//            return if (json != null) {
//                gson.fromJson(json, object : TypeToken<MutableList<Notification>>() {}.type)
//            } else {
//                mutableListOf()
//            }
//        }
//        private set(value) {
//            val json = gson.toJson(value)
//            sharedPreferences.edit().putString(paymentRequestKey, json).apply()
//        }
//
//    var invitationMessages: MutableList<Notification>
//        get() {
//            val json = sharedPreferences.getString(invitationMessagesKey, null)
//            return if (json != null) {
//                gson.fromJson(json, object : TypeToken<MutableList<Notification>>() {}.type)
//            } else {
//                mutableListOf()
//            }
//        }
//        private set(value) {
//            val json = gson.toJson(value)
//            sharedPreferences.edit().putString(invitationMessagesKey, json).apply()
//        }

    fun addPaymentSuccessNotification(notification: Notification) {
        val currentList = _paymentSuccessMessages.value.toMutableList()
        currentList.add(notification)
        _paymentSuccessMessages.value = currentList
        saveNotifications()
        updateAllNotifications()
        emitNewNotificationEvent()
    }

    fun addInvitationNotification(notification: Notification) {
        val currentList = _invitationMessages.value.toMutableList()
        currentList.add(notification)
        _invitationMessages.value = currentList
        saveNotifications()
        updateAllNotifications()
        emitNewNotificationEvent()
    }

    fun clearAllNotifications() {
        _paymentRequestMessages.value = emptyList()
        _invitationMessages.value = emptyList()
        saveNotifications()
        updateAllNotifications()
        coroutineScope.launch {
            EventBus.emit(NewNotificationEvent(false))
        }
    }

    private fun saveNotifications() {
        val paymentJson = gson.toJson(_paymentRequestMessages.value)
        val invitationJson = gson.toJson(_invitationMessages.value)
        val paymentSuccessJson = gson.toJson(_paymentSuccessMessages.value)
        sharedPreferences.edit().apply {
            putString(paymentRequestKey, paymentJson)
            putString(invitationMessagesKey, invitationJson)
            putString(paymentSuccessKey, paymentSuccessJson)
            apply()
        }
    }

    private fun emitNewNotificationEvent() {
        coroutineScope.launch {
            EventBus.emit(NewNotificationEvent(true))
        }
    }
}