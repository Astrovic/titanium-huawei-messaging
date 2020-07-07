package huawei.messaging

import com.huawei.agconnect.appmessaging.AGConnectAppMessaging
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnClickListener
import com.huawei.agconnect.appmessaging.model.AppMessage
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging
import org.appcelerator.kroll.KrollDict
import org.appcelerator.kroll.KrollModule
import org.appcelerator.kroll.annotations.Kroll.*
import org.appcelerator.kroll.common.Log
import org.appcelerator.titanium.TiApplication


class ClickListener : AGConnectAppMessagingOnClickListener {
    override fun onMessageClick(appMessage: AppMessage) {
        // Obtain the content of the tapped message.
    }
}

@module(name = "TitaniumHuaweiMessagingModule", id = "huawei.messaging")
class TitaniumHuaweiMessagingModule : KrollModule() {

    private lateinit var instance: AGConnectAppMessaging
    private var fcmToken: String = ""

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

    @getProperty
    @method
    fun getToken(): String {
        val appId = AGConnectServicesConfig.fromContext(TiApplication.getAppCurrentActivity()).getString("client/app_id")
        return HmsInstanceId.getInstance(TiApplication.getAppCurrentActivity()).getToken(appId, "HCM")
    }

    @method
    fun deleteToken() {
        val appId = AGConnectServicesConfig.fromContext(TiApplication.getAppCurrentActivity()).getString("client/app_id")
        HmsInstanceId.getInstance(TiApplication.getAppCurrentActivity()).deleteToken(appId, "HCM")
    }

    fun onTokenRefresh(token: String) {
        try {
            if (hasListeners("didRefreshRegistrationToken")) {
                val data = KrollDict()
                data["fcmToken"] = token
                fireEvent("didRefreshRegistrationToken", data)
                fcmToken = token
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