package huawei.messaging

import com.huawei.agconnect.appmessaging.AGConnectAppMessaging
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnClickListener
import com.huawei.agconnect.appmessaging.model.AppMessage
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessaging
import com.huawei.hms.push.RemoteMessage
import org.appcelerator.kroll.KrollDict
import org.appcelerator.kroll.KrollFunction
import org.appcelerator.kroll.KrollModule
import org.appcelerator.kroll.annotations.Kroll.*
import org.appcelerator.kroll.common.Log
import org.appcelerator.titanium.TiApplication
import java.util.*

class ClickListener : AGConnectAppMessagingOnClickListener {
    override fun onMessageClick(appMessage: AppMessage) {
        // Obtain the content of the tapped message.
    }
}

@module(name = "TitaniumHuaweiMessagingModule", id = "huawei.messaging")
class TitaniumHuaweiMessagingModule : KrollModule() {

    private lateinit var instance: AGConnectAppMessaging

    init {
        moduleInstance = this
    }

    @method
    fun configure() {
        instance = AGConnectAppMessaging.getInstance()
        instance.addOnClickListener(ClickListener())
    }

    @method
    fun trigger(event: String) {
        instance.trigger(event)
    }

    @method
    fun subscribe(topic: String) {
        HmsMessaging.getInstance(TiApplication.getAppCurrentActivity())
                .subscribe(topic)
                .addOnCompleteListener { task ->
                    val event = KrollDict()
                    event["success"] = task.isSuccessful
                    fireEvent("subscribe", event)
                }
    }

    @method
    fun unsubscribe(topic: String) {
        HmsMessaging.getInstance(TiApplication.getAppCurrentActivity())
                .unsubscribe(topic)
                .addOnCompleteListener { task ->
                    val event = KrollDict()
                    event["success"] = task.isSuccessful
                    fireEvent("subscribe", event)
                }
    }

    @setProperty
    fun enablePush(enabled: Boolean) {
        val messagingInstance = HmsMessaging.getInstance(TiApplication.getAppCurrentActivity())

        if (enabled) {
            messagingInstance.turnOnPush().addOnCompleteListener { _ ->
                // Handle task state?
            }
        } else {
            messagingInstance.turnOffPush().addOnCompleteListener { _ ->
                // Handle task state?
            }
        }
    }

    @method
    fun getToken(callback: KrollFunction) {
        object : Thread() {
            override fun run() {
                val event = KrollDict()

                try {
                    val appId = AGConnectServicesConfig.fromContext(TiApplication.getAppCurrentActivity()).getString("client/app_id")
                    val token = HmsInstanceId.getInstance(TiApplication.getAppCurrentActivity()).getToken(appId, "HCM")

                    event["success"] = true
                    event["token"] = token

                } catch (e: ApiException) {
                    Log.e("HCM", "Cannot get token: " + e.message)
                    event["success"] = false
                }

                callback.callAsync(getKrollObject(), event)
            }
        }.start()
    }

    @method
    fun deleteToken() {
        object : Thread() {
            override fun run() {
                try {
                    val appId = AGConnectServicesConfig.fromContext(TiApplication.getAppCurrentActivity()).getString("client/app_id")
                    HmsInstanceId.getInstance(TiApplication.getAppCurrentActivity()).deleteToken(appId, "HCM")
                } catch (e: ApiException) {
                    Log.e("HCM", "Cannot delete token: " + e.message)
                }
            }
        }.start()
    }

    fun onMessageReceived(message: RemoteMessage) {
        if (!hasListeners("didReceiveMessage")) { return }

        try {
            val data = KrollDict()
            data["message"] = message.notification.body
            fireEvent("didReceiveMessage", data)
        } catch (e: java.lang.Exception) {
            Log.e("HCM", "Message exception: " + e.message)
        }
    }

    fun onTokenRefresh(token: String) {
        try {
            if (hasListeners("didRefreshToken")) {
                val data = KrollDict()
                data["token"] = token
                fireEvent("didRefreshToken", data)
            }
        } catch (e: Exception) {
            Log.e("HMS", "Can't refresh token: " + e.message)
        }
    }

    companion object {
        lateinit var moduleInstance: TitaniumHuaweiMessagingModule

        fun getInstance(): TitaniumHuaweiMessagingModule? {
            return moduleInstance
        }
    }
}