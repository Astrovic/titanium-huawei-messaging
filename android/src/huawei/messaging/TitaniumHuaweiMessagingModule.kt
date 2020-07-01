package huawei.messaging

import com.huawei.agconnect.appmessaging.AGConnectAppMessaging
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnClickListener
import com.huawei.agconnect.appmessaging.model.AppMessage
import org.appcelerator.kroll.KrollDict
import org.appcelerator.kroll.KrollModule
import org.appcelerator.kroll.annotations.Kroll.method
import org.appcelerator.kroll.annotations.Kroll.module
import org.appcelerator.kroll.common.Log


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